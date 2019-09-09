package cn.roy.springcloud.api.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ExportUtil {
    private static final Logger logger = LoggerFactory.getLogger(ExportUtil.class);

    public static Map<String, Integer> getApiSet(String filePath) throws IOException {
        // 将文件读入
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }

        InputStream in = new FileInputStream(file);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(in);
        logger.info("sheet总共有{}个", xssfWorkbook.getNumberOfSheets());

        Map<String, Integer> urlMap = new HashMap<>();
        XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
        int index = 0;
        for (int i = 0; i < xssfSheet.getLastRowNum(); i++) {
            Row row = xssfSheet.getRow(i);
            if (row == null) {
                continue;
            }
            logger.info("第{}行总共有{}列", i, row.getLastCellNum());
            // 读取接口路径列，获取请求URL
            Cell cell = row.getCell(0);
            String url = cell.getStringCellValue();
            if (urlMap.keySet().contains(url)) {
                continue;
            }
            urlMap.put(url, index);
            index++;
        }
        logger.info("查询出的url为：{}", urlMap);

        return urlMap;
    }

}
