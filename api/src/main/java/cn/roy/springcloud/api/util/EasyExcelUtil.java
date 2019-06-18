package cn.roy.springcloud.api.util;

import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.annotation.ExcelColumnNum;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;

import java.io.*;
import java.util.List;

/**
 * @Description: 工具
 * @Author: Roy Z
 * @Date: 2019-05-24 09:39
 * @Version: v1.0
 */
public class EasyExcelUtil {

    public class ColumnValue {
        @ExcelColumnNum(1)
        private String s1;

        @ExcelColumnNum(2)
        private String s2;

        @ExcelColumnNum(3)
        private String s3;

        @ExcelColumnNum(4)
        private String s4;

        @ExcelColumnNum(5)
        private String s5;

        @ExcelColumnNum(6)
        private String s6;

        @ExcelColumnNum(7)
        private String s7;

        public ColumnValue() {
        }

        public ColumnValue(String s1, String s2, String s3, String s4, String s5, String s6, String s7) {
            this.s1 = s1;
            this.s2 = s2;
            this.s3 = s3;
            this.s4 = s4;
            this.s5 = s5;
            this.s6 = s6;
            this.s7 = s7;
        }

        @Override
        public String toString() {
            return "ColumnValue{" +
                    "s1='" + s1 + '\'' +
                    ", s2='" + s2 + '\'' +
                    ", s3='" + s3 + '\'' +
                    ", s4='" + s4 + '\'' +
                    ", s5='" + s5 + '\'' +
                    ", s6='" + s6 + '\'' +
                    ", s7='" + s7 + '\'' +
                    '}';
        }
    }

    public static void main(String[] args) {
        try (
                InputStream is = new FileInputStream("C:\\Users\\Roy Z Zhou\\Desktop\\Monthly Requirement Check_junjie.xlsx");
                BufferedInputStream inputStream = new BufferedInputStream(is);
        ) {
            // 解析每行结果在listener中处理
            ExcelListener listener = new ExcelListener();
            ExcelReader excelReader = new ExcelReader(inputStream, null, listener);
            excelReader.read();
            excelReader.getSheets();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class ExcelListener extends AnalysisEventListener<List<String>> {

        private Sheet currentSheet;

        @Override
        public void invoke(List<String> object, AnalysisContext context) {
            Sheet sheet = context.getCurrentSheet();
            System.out.println("名字：" + sheet.getSheetName());
            if (currentSheet == null) {
                currentSheet = sheet;
            }
            if (currentSheet != sheet) {
                // 读到另一个sheet
                currentSheet = sheet;
            }

            System.out.println(object.toString());
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {

        }
    }

}
