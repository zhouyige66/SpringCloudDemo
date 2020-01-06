package cn.roy.springcloud.api.service.impl;

import cn.roy.springcloud.api.bean.ApiBean;
import cn.roy.springcloud.api.bean.ParameterBean;
import cn.roy.springcloud.api.dao.mapper.ApiEntityModelMapper;
import cn.roy.springcloud.api.dao.mapper.ApiModelMapper;
import cn.roy.springcloud.api.dao.mapper.ApiParameterModelMapper;
import cn.roy.springcloud.api.dao.model.*;
import cn.roy.springcloud.api.service.SwaggerDataDealService;
import cn.roy.springcloud.api.util.ExportUtil;
import cn.roy.springcloud.base.http.ResultData;
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

import static cn.roy.springcloud.base.http.ResultData.CODE_DATA_NULL;
import static cn.roy.springcloud.base.http.ResultData.CODE_REQUEST_ERROR;

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
    private ApiModelMapper apiModelMapper;
    @Autowired
    private ApiEntityModelMapper apiEntityModelMapper;
    @Autowired
    private ApiParameterModelMapper apiParameterModelMapper;

    @Resource(name = "master")
    private DataSource dataSource;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultData importApiFromFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return ResultData.fail(CODE_REQUEST_ERROR, "文件不存在");
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
            return ResultData.requestError("链接指定URL出错，链接返回码：" + code);
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
            if (item.containsKey("properties")) {
                JSONObject propertiesJson = item.getJSONObject("properties");
                JSONObject bodySaveJson = new JSONObject();
                for (String property : propertiesJson.keySet()) {
                    JSONObject object = propertiesJson.getJSONObject(property);
                    bodySaveJson.put(property, object.getString("type") + "/" + object.getString("description"));
                }
                ApiEntityModel apiEntityModel = new ApiEntityModel();
                apiEntityModel.withName(key)
                        .withProperties(bodySaveJson.toString());
                apiEntityModelMapper.insertSelective(apiEntityModel);
            }
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
                        ApiEntityModelQuery query = new ApiEntityModelQuery();
                        if (schemaJson.containsKey("entity")) {
                            String ref = schemaJson.getString("entity");
                            String[] split = ref.split("/");
                            String entityName = split[split.length - 1];
                            logger.info("实体名称{}", entityName);

                            query.createCriteria().andNameEqualTo(entityName);
                            // 查询实体Id
                            List<ApiEntityModel> entities = apiEntityModelMapper.selectByQuery(query);
                            if (!CollectionUtils.isEmpty(entities)) {
                                bodyId = entities.get(0).getId();
                            }
                        } else {
                            // 对应body为基础类型如：String、Array等
                            String bodyName = paramJson.getString("name");
                            String entityDes = null;
                            if (paramJson.containsKey("description")) {
                                entityDes = paramJson.getString("description");
                            }
                            String type = schemaJson.getString("type");
                            if (type.equals("array")) {
                                // 解析子属性
                                JSONObject items = schemaJson.getJSONObject("items");
                                String itemType = items.getString("type");
                                if (items.containsKey("entity")) {
                                    String ref = items.getString("entity");
                                    String[] split = ref.split("/");
                                    String refName = split[split.length - 1];
                                    itemType = refName;
                                } else if (items.containsKey("format")) {
                                    String format = items.getString("format");
                                    if (format.equals("int64")) {
                                        itemType = "Long";
                                    }
                                }
                                type = "List<" + itemType + ">";
                            }
                            String searchKey = bodyName + (entityDes == null ? "" : ("@" + entityDes)) + "&" + type;
                            logger.info("搜索关键字：{}", searchKey);
                            query.createCriteria().andNameEqualTo(searchKey);
                            // 查询实体Id
                            List<ApiEntityModel> entities = apiEntityModelMapper.selectByQuery(query);
                            if (!CollectionUtils.isEmpty(entities)) {
                                bodyId = entities.get(0).getId();
                            } else {
                                // 未查到，直接插入数据库，然后赋值
                                ApiEntityModel apiEntityModel = new ApiEntityModel();
                                apiEntityModel.withName(searchKey).withProperties(type);
                                apiEntityModelMapper.insertSelective(apiEntityModel);
                                bodyId = apiEntityModel.getId();
                            }
                        }
                    } else {
                        ApiParameterModel apiParameterModel = new ApiParameterModel();
                        apiParameterModel.withName(paramJson.getString("name"))
                                .withDescription(des)
                                .withType(paramJson.getString("type"))
                                .withRequired(paramJson.getBoolean("required"));
                        apiParameterModelMapper.insertSelective(apiParameterModel);
                        long parameterId = apiParameterModel.getId();
                        parameterIdList.add(parameterId);
                    }
                }
            }
            // 查询对应的参数类型
            ApiModel api = new ApiModel();
            api.withName(key)
                    .withController(controller)
                    .withDescription(getOrPostJson.getString("summary"))
                    .withRoute("")
                    .withMethod(method)
                    .withBody(bodyId)
                    .withParameterids(JSON.toJSONString(parameterIdList));
            apiModelMapper.insertSelective(api);
        }

        return ResultData.success();
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
            return ResultData.fail(CODE_REQUEST_ERROR, "指定文件不存在");
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
            return ResultData.fail(CODE_REQUEST_ERROR, "查询接口列表为空");
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
        for (String key : urlMap.keySet()) {
            sortKeys[urlMap.get(key)] = key;
        }

        List<ApiBean> apiBeanList = new ArrayList<>();
        for (String url : sortKeys) {
            ApiModelQuery apiModelQuery = new ApiModelQuery();
            apiModelQuery.createCriteria().andNameEqualTo(url);
            List<ApiModel> apiModelList = apiModelMapper.selectByQuery(apiModelQuery);
            if (CollectionUtils.isEmpty(apiModelList)) {
                continue;
            }

            ApiModel apiModel = apiModelList.get(0);
            logger.info("查询api：" + JSON.toJSONString(apiModel));

            ApiBean apiBean = new ApiBean();
            apiBean.setName(apiModel.getName());
            apiBean.setController(apiModel.getController());
            apiBean.setDescription(apiModel.getDescription());
            apiBean.setMethod(apiModel.getMethod());
            // 参数处理
            String parameterids = apiModel.getParameterids();
            List<Long> parameterIdList = JSON.parseArray(parameterids, Long.class);
            if (!CollectionUtils.isEmpty(parameterIdList)) {
                List<ParameterBean> parameterBeanList = new ArrayList<>();
                for (Long pId : parameterIdList) {
                    ApiParameterModel parameter = apiParameterModelMapper.selectByPrimaryKey(pId);
                    logger.info("查询parameter：" + JSON.toJSONString(parameter));

                    ParameterBean parameterBean = new ParameterBean();
                    parameterBean.setName(parameter.getName());
                    parameterBean.setDescription(parameter.getDescription());
                    parameterBean.setRequired(parameter.getRequired());
                    parameterBean.setType(parameter.getType());

                    parameterBeanList.add(parameterBean);
                }
                apiBean.setParameterBeanList(parameterBeanList);
            }
            // body处理
            Long bodyId = apiModel.getBody();
            if (null != bodyId) {
                ApiEntityModel entity = apiEntityModelMapper.selectByPrimaryKey(bodyId);
                apiBean.setBodyDes(entity.getName());
                String bodyStr = entity.getProperties();
                if (bodyStr.startsWith("List")) {
                    // 读取具体类型
                    int startIndex = bodyStr.indexOf("<");
                    int endIndex = bodyStr.indexOf(">");
                    String body = bodyStr.substring(startIndex, endIndex);
                    // 搜索
                    ApiEntityModelQuery query = new ApiEntityModelQuery();
                    query.createCriteria().andNameEqualTo(body);
                    List<ApiEntityModel> entityList = apiEntityModelMapper.selectByQuery(query);
                    if (!CollectionUtils.isEmpty(entityList)) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("[").append(entityList.get(0).getProperties())
                                .append("]");
                        apiBean.setBody(sb.toString());
                    } else {
                        apiBean.setBody(bodyStr);
                    }
                } else {
                    apiBean.setBody(entity.getProperties());
                }
            }

            apiBeanList.add(apiBean);
        }

        if (CollectionUtils.isEmpty(apiBeanList)) {
            logger.info("查询数据为空，不作处理");
            return ResultData.fail(CODE_DATA_NULL, "未查出任何数据");
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
        write2Excel(sheet, apiBeanList, cellStyleBoldFont, cellStyleBackground);
        // 保存到电脑
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String fileName = "接口列表导出" + dateFormat.format(new Date()) + ".xlsx";
        String saveFilePath = "C:\\Users\\Roy Z Zhou\\Desktop\\" + fileName;
        // 将数据写入文件
        FileOutputStream out = new FileOutputStream(saveFilePath);
        workbook.write(out);

        return ResultData.success();
    }

    private void write2Excel(XSSFSheet sheet, List<ApiBean> apiBeanList, XSSFCellStyle boldFontStyle,
                             XSSFCellStyle backgroundStyle) {
        if (CollectionUtils.isEmpty(apiBeanList)) {
            return;
        }

        int index = 0;
        for (ApiBean apiBean : apiBeanList) {
            XSSFRow row = sheet.createRow(index);
            createCell(row);
            for (int i = 0; i < 5; i++) {
                row.getCell(i).setCellStyle(backgroundStyle);
            }
            sheet.addMergedRegion(new CellRangeAddress(index, index, 1, 4));
            row.getCell(0).setCellValue("接口名称");

            row.getCell(1).setCellValue(apiBean.getName());
            index++;

            XSSFRow row2 = sheet.createRow(index);
            createCell(row2);
            sheet.addMergedRegion(new CellRangeAddress(index, index, 1, 4));
            row2.getCell(0).setCellValue("接口功能");
            row2.getCell(0).setCellStyle(boldFontStyle);
            row2.getCell(1).setCellValue(apiBean.getDescription());
            index++;

            XSSFRow row3 = sheet.createRow(index);
            createCell(row3);
            sheet.addMergedRegion(new CellRangeAddress(index, index, 1, 4));
            row3.getCell(0).setCellValue("请求方法");
            row3.getCell(0).setCellStyle(boldFontStyle);
            row3.getCell(1).setCellValue(apiBean.getMethod());
            index++;

            List<ParameterBean> parameterBeanList = apiBean.getParameterBeanList();
            if (!CollectionUtils.isEmpty(parameterBeanList)) {
                int startIndex = index;
                // 添加提示头
                XSSFRow row4 = sheet.createRow(index);
                createCell(row4);
                row4.getCell(1).setCellValue("参数名");
                row4.getCell(2).setCellValue("描述");
                row4.getCell(3).setCellValue("参数类型");
                row4.getCell(4).setCellValue("是否必填");
                index++;

                for (int i = 0; i < parameterBeanList.size(); i++) {
                    ParameterBean parameterBean = parameterBeanList.get(i);

                    XSSFRow itemRow = sheet.createRow(index);
                    createCell(itemRow);
                    // 赋值
                    itemRow.getCell(1).setCellValue(parameterBean.getName());
                    itemRow.getCell(2).setCellValue(parameterBean.getDescription());
                    itemRow.getCell(3).setCellValue(parameterBean.getType());
                    itemRow.getCell(4).setCellValue(parameterBean.isRequired());
                    index++;
                }

                sheet.addMergedRegion(new CellRangeAddress(startIndex, index - 1, 0, 0));
                row4.getCell(0).setCellValue("请求参数(query)");
                row4.getCell(0).setCellStyle(boldFontStyle);
            }

            String body = apiBean.getBody();
            if (!StringUtils.isEmpty(body)) {
                XSSFRow row5 = sheet.createRow(index);
                createCell(row5);
                sheet.addMergedRegion(new CellRangeAddress(index, index, 3, 4));
                row5.getCell(0).setCellValue("请求body");
                String bodyDes = apiBean.getBodyDes();
                String nameAndDes = bodyDes.split("&")[0];
                String[] split = nameAndDes.split("@");
                row5.getCell(0).setCellStyle(boldFontStyle);
                row5.getCell(1).setCellValue(split[0]);
                if (split.length > 2) {
                    row5.getCell(2).setCellValue(split[1]);
                }
                row5.getCell(3).setCellValue(body);
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
                    "TRUNCATE TABLE api_entity;" +
                    "TRUNCATE TABLE api_parameter";
            statement.execute(sql);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return ResultData.fail(CODE_REQUEST_ERROR, e.getMessage());
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

        return ResultData.success();
    }

}
