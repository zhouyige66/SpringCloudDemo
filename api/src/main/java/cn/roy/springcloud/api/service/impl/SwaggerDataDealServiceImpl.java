package cn.roy.springcloud.api.service.impl;

import cn.roy.springcloud.api.dao.bean.*;
import cn.roy.springcloud.api.dao.mapper.ApiMapper;
import cn.roy.springcloud.api.dao.mapper.EntityMapper;
import cn.roy.springcloud.api.dao.mapper.ParameterMapper;
import cn.roy.springcloud.api.entity.ApiEntity;
import cn.roy.springcloud.api.entity.ParameterEntity;
import cn.roy.springcloud.api.entity.ResultData;
import cn.roy.springcloud.api.service.SwaggerDataDealService;
import cn.roy.springcloud.api.util.ExportUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

import static cn.roy.springcloud.api.entity.ResultData.CODE_PARAMETER_ERROR;
import static cn.roy.springcloud.api.entity.ResultData.CODE_SERVER_ERROR;

/**
 * @Description: 数据导入处理
 * @Author: Roy Z
 * @Date: 2019-09-05 18:40
 * @Version: v1.0
 */
@Service
public class SwaggerDataDealServiceImpl implements SwaggerDataDealService {
    private static final Logger logger = LoggerFactory.getLogger(SwaggerDataDealService.class);

    @Autowired
    private ApiMapper apiMapper;
    @Autowired
    private EntityMapper entityMapper;
    @Autowired
    private ParameterMapper parameterMapper;

    @Resource(name = "master")
    private DataSource dataSource;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultData importApiFromFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return ResultData.fail(CODE_PARAMETER_ERROR, "文件不存在");
        }

        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file),
                StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuffer sb = new StringBuffer();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        String data = sb.toString();

        return dealJsonData(data);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultData importApiFromUrl(String swaggerUrl) throws URISyntaxException, IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpUriRequest request = new HttpGet(new URI(swaggerUrl));
        HttpResponse httpResponse = httpClient.execute(request);
        int code = httpResponse.getStatusLine().getStatusCode();
        if (code == 200) {
            HttpEntity httpResponseEntity = httpResponse.getEntity();
            InputStream content = httpResponseEntity.getContent();
            BufferedReader rd = new BufferedReader(new InputStreamReader(content));
            StringBuilder data = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                data.append(line);
            }
            String swaggerData = data.toString();

            return dealJsonData(swaggerData);
        } else {
            logger.info("返回编码：" + code);
            return ResultData.serverError("链接指定URL出错，链接返回码：" + code);
        }
    }

    private ResultData dealJsonData(String data) {
        data = data.replaceAll("[$]ref", "entity");
        JSONObject jsonObject = JSON.parseObject(data);

        // 存储实体
        JSONObject entityJson = jsonObject.getJSONObject("definitions");
        Set<String> entityKeySet = entityJson.keySet();
        for (String key : entityKeySet) {
            JSONObject item = entityJson.getJSONObject(key);

            JSONObject propertiesJson = item.getJSONObject("properties");
            JSONObject bodySaveJson = new JSONObject();
            for (String property : propertiesJson.keySet()) {
                JSONObject object = propertiesJson.getJSONObject(property);
                bodySaveJson.put(property, object.getString("type") + "/" + object.getString("description"));
            }
            Entity entity = new Entity();
            entity.withName(key)
                    .withProperties(bodySaveJson.toString());
            entityMapper.insertSelective(entity);
        }

        // 存储api以及参数

        JSONObject pathsJson = jsonObject.getJSONObject("paths");
        Set<String> keySet = pathsJson.keySet();
        for (String key : keySet) {
            JSONObject itemJson = pathsJson.getJSONObject(key);
            String method = new ArrayList<>(itemJson.keySet()).get(0);

            JSONObject getOrPostJson = itemJson.getJSONObject(method);
            String controller = getOrPostJson.getJSONArray("tags").getString(0);
            logger.info("api:{},请求方法：{}，对应{}", key, method, controller);

            // 处理参数
            JSONArray parametersJSONArray = getOrPostJson.getJSONArray("parameters");
            List<Long> parameterIdList = new ArrayList<>();
            Long bodyId = null;
            if (parametersJSONArray != null && parametersJSONArray.size() > 0) {
                for (int i = 0; i < parametersJSONArray.size(); i++) {
                    JSONObject paramJson = parametersJSONArray.getJSONObject(i);

                    String in = paramJson.getString("in");
                    String des = paramJson.getString("description");
                    if (in.equals("body")) {
                        JSONObject schemaJson = JSON.parseObject(paramJson.getString("schema"));
                        if (schemaJson.containsKey("entity")) {
                            String ref = schemaJson.getString("entity");
                            String[] split = ref.split("/");

                            String entityName = split[split.length - 1];
                            logger.info("实体名称{}", entityName);
                            EntityQuery query = new EntityQuery();
                            query.getOredCriteria().add(
                                    new EntityQuery().createCriteria().andNameEqualTo(entityName));
                            // 查询实体Id
                            List<Entity> entities = entityMapper.selectByExample(query);
                            if (entities != null && entities.size() > 0) {
                                bodyId = entities.get(0).getId();
                            }
                        }
                    } else {
                        Parameter parameter = new Parameter();
                        parameter.withName(paramJson.getString("name"))
                                .withDescription(des)
                                .withType(paramJson.getString("type"))
                                .withRequired(paramJson.getBoolean("required"));
                        parameterMapper.insertSelective(parameter);
                        long parameterId = parameter.getId();
                        parameterIdList.add(parameterId);
                    }
                }
            }
            // 查询对应的参数类型
            Api api = new Api();
            api.withName(key)
                    .withController(controller)
                    .withDescription(getOrPostJson.getString("summary"))
                    .withRoute("")
                    .withMethod(method)
                    .withBody(bodyId)
                    .withParameterids(JSON.toJSONString(parameterIdList));

            apiMapper.insertSelective(api);
        }

        return ResultData.success(null);
    }

    /**
     * 导出指定文件中中接口
     *
     * @param filePath eg:String filePath = "C:\Users\Roy Z Zhou\Desktop\接口列表.xlsx";
     * @return
     */
    @Override
    public ResultData exportApiDoc(String filePath) {
        Map<String, Integer> urlMap = null;
        try {
            urlMap = ExportUtil.getApiSet(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (MapUtils.isEmpty(urlMap)) {
            return ResultData.fail(CODE_PARAMETER_ERROR, "指定文件不存在");
        }

        try {
            return exportDoc(urlMap);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultData.serverError(e.getMessage());
        }
    }

    @Override
    public ResultData exportApiDoc(List<String> apiNameList) {
        if (CollectionUtils.isEmpty(apiNameList)) {
            return ResultData.fail(CODE_PARAMETER_ERROR, "查询接口列表为空");
        }

        Map<String, Integer> urlMap = new HashedMap<>();
        int index = 0;
        for (String url : apiNameList) {
            if (urlMap.keySet().contains(url)) {
                continue;
            }
            urlMap.put(url, index);
            index++;
        }
        try {
            return exportDoc(urlMap);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultData.serverError(e.getMessage());
        }
    }

    private ResultData exportDoc(Map<String, Integer> urlMap) throws IOException {
        // 排序处理
        String[] sortKeys = new String[urlMap.keySet().size()];
        for(String key:urlMap.keySet()){
            sortKeys[urlMap.get(key)] = key;
        }

        List<ApiEntity> apiEntityList = new ArrayList<>();
        for (String url : sortKeys) {
            ApiQuery apiQuery = new ApiQuery();
            apiQuery.createCriteria().andNameEqualTo(url);
            List<Api> apiList = apiMapper.selectByExample(apiQuery);
            if (CollectionUtils.isEmpty(apiList)) {
                continue;
            }

            Api api = apiList.get(0);
            logger.info("查询api：" + JSON.toJSONString(api));

            ApiEntity apiEntity = new ApiEntity();
            apiEntity.setName(api.getName());
            apiEntity.setController(api.getController());
            apiEntity.setDescription(api.getDescription());
            apiEntity.setMethod(api.getMethod());
            // 参数处理
            String parameterids = api.getParameterids();
            List<Long> parameterIdList = JSON.parseArray(parameterids, Long.class);
            if (!CollectionUtils.isEmpty(parameterIdList)) {
                List<ParameterEntity> parameterEntityList = new ArrayList<>();
                for (Long pId : parameterIdList) {
                    Parameter parameter = parameterMapper.selectByPrimaryKey(pId);
                    logger.info("查询parameter：" + JSON.toJSONString(parameter));

                    ParameterEntity parameterEntity = new ParameterEntity();
                    parameterEntity.setName(parameter.getName());
                    parameterEntity.setDescription(parameter.getDescription());
                    parameterEntity.setRequired(parameter.getRequired());
                    parameterEntity.setType(parameter.getType());

                    parameterEntityList.add(parameterEntity);
                }
                apiEntity.setParameterEntityList(parameterEntityList);
            }
            // body处理
            Long bodyId = api.getBody();
            if (null != bodyId) {
                Entity entity = entityMapper.selectByPrimaryKey(bodyId);
                logger.info("查询body：" + JSON.toJSONString(entity));

                apiEntity.setBody(entity.getProperties());
            }

            apiEntityList.add(apiEntity);
        }

        if (CollectionUtils.isEmpty(apiEntityList)) {
            logger.info("查询数据为空，不作处理");
            return ResultData.fail(CODE_SERVER_ERROR, "未查出任何数据");
        }

        // 创建新文件
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();
        // 集中常见style
        // 1.默认
        XSSFCellStyle cellStyleDefault = workbook.createCellStyle();
        cellStyleDefault.setFillBackgroundColor(IndexedColors.WHITE.index);
        cellStyleDefault.setFillPattern(FillPatternType.NO_FILL);
        cellStyleDefault.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyleDefault.setWrapText(true);
        // 2.字体加粗
        XSSFCellStyle cellStyleBoldFont = workbook.createCellStyle();
        cellStyleBoldFont.cloneStyleFrom(cellStyleDefault);
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);//设置字体大小
        font.setBold(true);
        cellStyleBoldFont.setFont(font);
        // 3.背景色
        XSSFCellStyle cellStyleBackground = workbook.createCellStyle();
        cellStyleBackground.cloneStyleFrom(cellStyleBoldFont);
        cellStyleBackground.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyleBackground.setFillForegroundColor(IndexedColors.GOLD.index);
        cellStyleBackground.setFillBackgroundColor(IndexedColors.GOLD.index);
        cellStyleBoldFont.setFont(font);

        // 设置列宽
        sheet.setDefaultColumnWidth(24);
        sheet.setColumnWidth(5, 12 * 256);
        // 设置行高
        sheet.setDefaultRowHeight((short) (20 * 20));
        // 写到excel中
        write2Excel(sheet, apiEntityList, cellStyleBoldFont, cellStyleBackground);
        // 保存到电脑
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String fileName = "接口列表导出" + dateFormat.format(new Date()) + ".xlsx";
        String saveFilePath = "C:\\Users\\Roy Z Zhou\\Desktop\\" + fileName;
        // 将数据写入文件
        FileOutputStream out = new FileOutputStream(saveFilePath);
        workbook.write(out);

        return ResultData.success(null);
    }

    private void write2Excel(XSSFSheet sheet, List<ApiEntity> apiEntityList, XSSFCellStyle boldFontStyle,
                             XSSFCellStyle backgroundStyle) {
        if (CollectionUtils.isEmpty(apiEntityList)) {
            return;
        }

        int index = 0;
        for (ApiEntity apiEntity : apiEntityList) {
            XSSFRow row = sheet.createRow(index);
            createCell(row);
            for (int i = 0; i < 5; i++) {
                row.getCell(i).setCellStyle(backgroundStyle);
            }
            sheet.addMergedRegion(new CellRangeAddress(index, index, 1, 4));
            row.getCell(0).setCellValue("接口名称");

            row.getCell(1).setCellValue(apiEntity.getName());
            index++;

            XSSFRow row2 = sheet.createRow(index);
            createCell(row2);
            sheet.addMergedRegion(new CellRangeAddress(index, index, 1, 4));
            row2.getCell(0).setCellValue("接口功能");
            row2.getCell(0).setCellStyle(boldFontStyle);
            row2.getCell(1).setCellValue(apiEntity.getDescription());
            index++;

            XSSFRow row3 = sheet.createRow(index);
            createCell(row3);
            sheet.addMergedRegion(new CellRangeAddress(index, index, 1, 4));
            row3.getCell(0).setCellValue("请求方法");
            row3.getCell(0).setCellStyle(boldFontStyle);
            row3.getCell(1).setCellValue(apiEntity.getMethod());
            index++;

            List<ParameterEntity> parameterEntityList = apiEntity.getParameterEntityList();
            if (!CollectionUtils.isEmpty(parameterEntityList)) {
                int startIndex = index;
                // 添加提示头
                XSSFRow row4 = sheet.createRow(index);
                createCell(row4);
                row4.getCell(1).setCellValue("参数名");
                row4.getCell(2).setCellValue("描述");
                row4.getCell(3).setCellValue("参数类型");
                row4.getCell(4).setCellValue("是否必填");
                index++;

                for (int i = 0; i < parameterEntityList.size(); i++) {
                    ParameterEntity parameterEntity = parameterEntityList.get(i);

                    XSSFRow itemRow = sheet.createRow(index);
                    createCell(itemRow);
                    // 赋值
                    itemRow.getCell(1).setCellValue(parameterEntity.getName());
                    itemRow.getCell(2).setCellValue(parameterEntity.getDescription());
                    itemRow.getCell(3).setCellValue(parameterEntity.getType());
                    itemRow.getCell(4).setCellValue(parameterEntity.isRequired());
                    index++;
                }

                sheet.addMergedRegion(new CellRangeAddress(startIndex, index - 1, 0, 0));
                row4.getCell(0).setCellValue("请求参数(query)");
                row4.getCell(0).setCellStyle(boldFontStyle);
            }

            String body = apiEntity.getBody();
            if (!StringUtils.isEmpty(body)) {
                XSSFRow row5 = sheet.createRow(index);
                createCell(row5);
                sheet.addMergedRegion(new CellRangeAddress(index, index, 1, 4));
                row5.getCell(0).setCellValue("请求body");
                row5.getCell(0).setCellStyle(boldFontStyle);
                row5.getCell(1).setCellValue(body);
                index++;
            }

            // 接口间空一行
            XSSFRow rowBlank = sheet.createRow(index);
            createCell(rowBlank);
            sheet.addMergedRegion(new CellRangeAddress(index, index, 0, 4));
            index++;
        }
    }


    private void createCell(XSSFRow row) {
        for (int i = 0; i < 5; i++) {
            row.createCell(i);
        }
    }

    /**
     * 清除数据库数据
     *
     * @return
     */
    @Override
    public ResultData cleanData() {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            // 清除三张表数据
            String sql = "TRUNCATE TABLE api;" +
                    "TRUNCATE TABLE entity;" +
                    "TRUNCATE TABLE parameter";
            statement.execute(sql);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return ResultData.fail(CODE_SERVER_ERROR, e.getMessage());
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
                if (statement != null && statement.isClosed()) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return ResultData.success(null);
    }

}
