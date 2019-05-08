package cn.roy.springcloud.api.util;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description: Excel导入导出工具
 * @Author: Roy Z
 * @Date: 2019-04-29 10:30
 * @Version: v1.0
 */
public final class ExcelUtil {

    private static void createExcel(String filePath, Class<?> excelType) {
        try {
            Object instance = excelType.newInstance();
            Sheet sheet = null;
            CellStyle cellStyle = null;
            CellStyle cellStyleDark = null;
            Font font = null;
            if (instance instanceof HSSFWorkbook) {
                sheet = ((HSSFWorkbook) instance).createSheet();
                cellStyle = ((HSSFWorkbook) instance).createCellStyle();
                cellStyleDark = ((HSSFWorkbook) instance).createCellStyle();
                font = ((HSSFWorkbook) instance).createFont();
            } else if (instance instanceof XSSFWorkbook) {
                sheet = ((XSSFWorkbook) instance).createSheet();
                cellStyle = ((XSSFWorkbook) instance).createCellStyle();
                cellStyleDark = ((XSSFWorkbook) instance).createCellStyle();
                font = ((XSSFWorkbook) instance).createFont();
            }
            // 设置列宽
            sheet.setDefaultColumnWidth(24);
            sheet.setColumnWidth(5, 12 * 256);
            // 设置行高
            sheet.setDefaultRowHeight((short) (20 * 20));
            // 合并单元格
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 3));
            sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, 5));
            sheet.addMergedRegion(new CellRangeAddress(5, 5, 0, 5));
            sheet.addMergedRegion(new CellRangeAddress(6, 7, 0, 5));
            // 设置要添加表格背景颜色
            cellStyle.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.index);
            // solid 填充
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            // 文字垂直居中
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            // 拷贝cell样式
            cellStyleDark.cloneStyleFrom(cellStyle);
            font.setFontHeightInPoints((short) 10);//设置字体大小
            font.setBold(true);
            cellStyleDark.setFont(font);

            // 为单元格添加背景样式
            for (int i = 0; i < 8; i++) {
                // 创建行
                Row row = sheet.createRow(i);
                for (int j = 0; j < 7; j++) {
                    // 创建列
                    Cell cell = row.createCell(j);
                    if (i == 3) {// 第四行统一添加下划线
                        CellStyle cellStyleWithBorder = null;
                        if (instance instanceof HSSFWorkbook) {
                            cellStyleWithBorder = ((HSSFWorkbook) instance).createCellStyle();
                        } else if (instance instanceof XSSFWorkbook) {
                            cellStyleWithBorder = ((XSSFWorkbook) instance).createCellStyle();
                        }
                        cellStyleWithBorder.cloneStyleFrom(cellStyle);
                        cellStyleWithBorder.setBorderBottom(BorderStyle.THIN);
                        cellStyleWithBorder.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.index);
                        if (j == 4) {
                            cellStyleWithBorder.setFont(font);
                        }
                        cell.setCellStyle(cellStyleWithBorder);
                    } else {
                        cell.setCellStyle(cellStyle);
                    }
                }
                // 合并单元格，cellRangAddress四个参数，第一个起始行，第二终止行，第三个起始列，第四个终止列
                if (i < 3) {
                    sheet.addMergedRegion(new CellRangeAddress(i, i, 1, 3));
                }
            }

            // 添加数据
            Row row1 = sheet.getRow(0);
            row1.getCell(0).setCellStyle(cellStyleDark);
            row1.getCell(4).setCellStyle(cellStyleDark);
            row1.getCell(0).setCellValue("状态:");
            row1.getCell(1).setCellValue("New");
            row1.getCell(4).setCellValue("开始日期:");
            Row row2 = sheet.getRow(1);
            row2.getCell(0).setCellStyle(cellStyleDark);
            row2.getCell(4).setCellStyle(cellStyleDark);
            row2.getCell(0).setCellValue("优先级:");
            row2.getCell(1).setCellValue("Normal");
            row2.getCell(4).setCellValue("计划完成时间:");
            Row row3 = sheet.getRow(2);
            row3.getCell(0).setCellStyle(cellStyleDark);
            row3.getCell(4).setCellStyle(cellStyleDark);
            CellStyle cellStyleWithBackground = null;
            if (instance instanceof HSSFWorkbook) {
                cellStyleWithBackground = ((HSSFWorkbook) instance).createCellStyle();
            } else if (instance instanceof XSSFWorkbook) {
                cellStyleWithBackground = ((XSSFWorkbook) instance).createCellStyle();
            }
            cellStyleWithBackground.cloneStyleFrom(cellStyle);
            cellStyleWithBackground.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
            row3.getCell(5).setCellStyle(cellStyleWithBackground);
            row3.getCell(0).setCellValue("指派给:");
            row3.getCell(1).setCellValue("陶明龙");
            row3.getCell(4).setCellValue("%完成:");
            row3.getCell(6).setCellValue("0%");
            Row row4 = sheet.getRow(3);
            row4.getCell(4).setCellValue("期望时间:");
            row4.setHeight((short) (25 * 20));
            Row row5 = sheet.getRow(4);
            row5.setHeight((short) (25 * 20));
            row5.getCell(0).setCellStyle(cellStyleDark);
            row5.getCell(0).setCellValue("描述:");
            Row row6 = sheet.getRow(5);
            row6.getCell(0).setCellValue("修复BUG：弱网情况下，reviewer点击reviewed按钮会触发多次SDC TIME PUSH；");

            // 将数据写入文件
            FileOutputStream out = new FileOutputStream(filePath);
            if (instance instanceof HSSFWorkbook) {
                ((HSSFWorkbook) instance).write(out);
            } else if (instance instanceof XSSFWorkbook) {
                ((XSSFWorkbook) instance).write(out);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createExcelFile(String fileName) throws IOException {
        createExcel(fileName, HSSFWorkbook.class);
    }

    public static void createExcelForAdvance(String fileName) throws IOException {
        createExcel(fileName, XSSFWorkbook.class);
    }

    public static void readExcel(String excelName) throws IOException {
        //将文件读入
        InputStream in = new FileInputStream(new File(excelName));
        //创建工作簿
        HSSFWorkbook wb = new HSSFWorkbook(in);
        //读取第一个sheet
        Sheet sheet = wb.getSheetAt(0);
        //获取第二行
        Row row = sheet.getRow(1);
        //循环读取科目
        for (int i = 1; i < 6; i++) {
            System.out.println(row.getCell(i));
        }
    }

    public static void readExcelAdvance(String excelName) throws IOException {
        // 将文件读入
        InputStream in = new FileInputStream(new File(excelName));
        // 创建工作簿
        XSSFWorkbook wb = new XSSFWorkbook(in);
        System.out.println("列：" + wb.getNumberOfSheets());

        // 创建新文件
        String filePath = "C:\\Users\\Roy Z Zhou\\Desktop\\log.xls";
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet hssfSheet = hssfWorkbook.createSheet();
        // 设置列宽
        hssfSheet.setDefaultColumnWidth(24);
        hssfSheet.setColumnWidth(5, 12 * 256);
        // 设置行高
        hssfSheet.setDefaultRowHeight((short) (20 * 20));

        // 默认
        HSSFCellStyle cellStyleDefault = hssfWorkbook.createCellStyle();
        cellStyleDefault.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.index);
        cellStyleDefault.setFillBackgroundColor(IndexedColors.LEMON_CHIFFON.index);
        cellStyleDefault.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyleDefault.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyleDefault.setWrapText(true);

        // 顶部对齐
        HSSFCellStyle cellStyleAlignment = hssfWorkbook.createCellStyle();
        cellStyleAlignment.cloneStyleFrom(cellStyleDefault);
        cellStyleAlignment.setVerticalAlignment(VerticalAlignment.TOP);

        // 字体加粗
        HSSFCellStyle cellStyleDark = hssfWorkbook.createCellStyle();
        cellStyleDark.cloneStyleFrom(cellStyleDefault);
        HSSFFont font = hssfWorkbook.createFont();
        font.setFontHeightInPoints((short) 10);//设置字体大小
        font.setBold(true);
        cellStyleDark.setFont(font);

        // 纯色背景
        HSSFCellStyle cellStyleWithBackground = hssfWorkbook.createCellStyle();
        cellStyleWithBackground.cloneStyleFrom(cellStyleDefault);
        cellStyleWithBackground.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);

        // 带下划线
        HSSFCellStyle cellStyleWithBorder = hssfWorkbook.createCellStyle();
        cellStyleWithBorder.cloneStyleFrom(cellStyleDark);
        cellStyleWithBorder.setBorderBottom(BorderStyle.THIN);
        cellStyleWithBorder.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.index);

        cellStyleWithBackground.setFont(font);
        int sheetCount = wb.getNumberOfSheets();
        int j = 0;
        for (int k = 0; k < sheetCount; k++) {
            // 读取sheet
            Sheet sheet = wb.getSheetAt(k);
            for (int i = 0; i < sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                Cell cell = row.getCell(0);
                if (cell.getCellType() == null || cell.getCellType() == CellType.STRING) {
                    continue;
                }
                createRowAndCell(hssfSheet, cellStyleDefault, cellStyleAlignment, cellStyleDark, cellStyleWithBackground, cellStyleWithBorder,
                        row, j);
                j++;
            }
        }
        // 将数据写入文件
        FileOutputStream out = new FileOutputStream(filePath);
        hssfWorkbook.write(out);
    }

    private static void createRowAndCell(Sheet sheet, CellStyle cellStyleDefault, CellStyle cellStyleAlignment,
                                         CellStyle cellStyleDark, CellStyle cellStyleWithBackground,
                                         CellStyle cellStyleWithBorder, Row rowValue, int index) {
        Cell cellTime = rowValue.getCell(0);
        Cell cellDes = rowValue.getCell(2);
        String time = "";
        String des = cellDes.getStringCellValue();
        if (cellTime.getCellType() == CellType.NUMERIC) {
            short format = cellTime.getCellStyle().getDataFormat();
            SimpleDateFormat sdf = null;
            if (format == 14 || format == 31 || format == 57 || format == 58) {
                //日期
                sdf = new SimpleDateFormat("yyyy-MM-dd");
            } else if (format == 20 || format == 32) {
                //时间
                sdf = new SimpleDateFormat("HH:mm");
            }
            double value = cellTime.getNumericCellValue();
            Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(value);
            time = sdf.format(date);
        }

        int rows = 6;
        for (int i = 0; i < rows; i++) {
            // 创建行
            int rowIndex = index * rows + i;
            Row newRow = sheet.createRow(rowIndex);
            for (int j = 0; j < 12; j++) {
                // 创建列
                Cell cell = newRow.createCell(j);
                if (i == 3) {// 第四行统一添加下划线
                    cell.setCellStyle(cellStyleWithBorder);
                } else if (i == 5) {
                    cell.setCellStyle(cellStyleAlignment);
                } else {
                    cell.setCellStyle(cellStyleDefault);
                }
            }
            if (i == 5) {
                newRow.setHeight((short) (100 * 20));
            }
            // 合并单元格
            switch (i) {
                case 3:
                    newRow.setHeight((short) (25 * 20));
                    sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 3));
                    break;
                case 4:
                case 5:
                    sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 5));
                    break;
                default:
                    sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 1, 3));
                    break;
            }
        }

        int beginIndex = index * rows;
        // 添加数据
        Row row1 = sheet.getRow(beginIndex);
        row1.getCell(0).setCellStyle(cellStyleDark);
        row1.getCell(4).setCellStyle(cellStyleDark);
        row1.getCell(0).setCellValue("状态:");
        row1.getCell(1).setCellValue("New");
        row1.getCell(4).setCellValue("开始日期:");
        row1.getCell(5).setCellValue(time);

        Row row2 = sheet.getRow(beginIndex + 1);
        row2.getCell(0).setCellStyle(cellStyleDark);
        row2.getCell(4).setCellStyle(cellStyleDark);
        row2.getCell(0).setCellValue("优先级:");
        row2.getCell(1).setCellValue("Normal");
        row2.getCell(4).setCellValue("计划完成时间:");

        Row row3 = sheet.getRow(beginIndex + 2);
        row3.getCell(0).setCellStyle(cellStyleDark);
        row3.getCell(4).setCellStyle(cellStyleDark);
        row3.getCell(5).setCellStyle(cellStyleWithBackground);
        row3.getCell(0).setCellValue("指派给:");
        row3.getCell(1).setCellValue("陶明龙");
        row3.getCell(4).setCellValue("%完成:");
        row3.getCell(6).setCellValue("0%");

        Row row4 = sheet.getRow(beginIndex + 3);
        row4.getCell(4).setCellValue("期望时间:");

        Row row5 = sheet.getRow(beginIndex + 4);
        row5.setHeight((short) (25 * 20));
        row5.getCell(0).setCellStyle(cellStyleDark);
        row5.getCell(0).setCellValue("描述:");

        Row row6 = sheet.getRow(beginIndex + 5);
        row6.getCell(0).setCellValue(des);
    }

    public static void main(String[] args) {
        String filePath1 = "C:\\Users\\Roy Z Zhou\\Desktop\\Monthly Requirement Check_junjie.xlsx";
        try {
            readExcelAdvance(filePath1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
