package com.qindashuai.toolkit.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
public class ExcelExportUtil {

    private static final int DEFAULT_WINDOW_SIZE = 1000;
    private static final int MAX_ROW_ACCESS = 5000;

    public <T> void export(HttpServletResponse response, String fileName, String sheetName,
                           List<T> dataList, Class<T> clazz) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()).replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename=" + encodedFileName + ".xlsx");
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");

        try (OutputStream out = response.getOutputStream()) {
            export(out, fileName, sheetName, dataList, clazz);
        }
    }

    public <T> void export(OutputStream out, String fileName, String sheetName,
                           List<T> dataList, Class<T> clazz) throws IOException {
        List<ExcelFieldInfo> fieldInfoList = resolveExcelFields(clazz);
        if (fieldInfoList.isEmpty()) {
            throw new IllegalArgumentException("类 " + clazz.getName() + " 中没有标注 @ExcelColumn 的字段");
        }

        try (SXSSFWorkbook workbook = new SXSSFWorkbook(DEFAULT_WINDOW_SIZE)) {
            workbook.setCompressTempFiles(true);
            Sheet sheet = workbook.createSheet(StringUtils.hasText(sheetName) ? sheetName : "Sheet1");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            createHeaderRow(sheet, fieldInfoList, headerStyle);

            if (dataList != null && !dataList.isEmpty()) {
                fillDataRows(sheet, fieldInfoList, dataList, dataStyle, workbook);
            }

            for (int i = 0; i < fieldInfoList.size(); i++) {
                int width = fieldInfoList.get(i).getWidth() * 256;
                sheet.setColumnWidth(i, Math.max(width, 3000));
            }

            workbook.write(out);
            out.flush();
        }
    }

    public <T> void exportBatch(HttpServletResponse response, String fileName, String sheetName,
                                List<List<T>> batchDataList, Class<T> clazz) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()).replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename=" + encodedFileName + ".xlsx");
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");

        try (OutputStream out = response.getOutputStream()) {
            exportBatch(out, fileName, sheetName, batchDataList, clazz);
        }
    }

    public <T> void exportBatch(OutputStream out, String fileName, String sheetName,
                                List<List<T>> batchDataList, Class<T> clazz) throws IOException {
        List<ExcelFieldInfo> fieldInfoList = resolveExcelFields(clazz);
        if (fieldInfoList.isEmpty()) {
            throw new IllegalArgumentException("类 " + clazz.getName() + " 中没有标注 @ExcelColumn 的字段");
        }

        try (SXSSFWorkbook workbook = new SXSSFWorkbook(MAX_ROW_ACCESS)) {
            workbook.setCompressTempFiles(true);
            SXSSFSheet sheet = workbook.createSheet(StringUtils.hasText(sheetName) ? sheetName : "Sheet1");
            sheet.setRandomAccessWindowSize(DEFAULT_WINDOW_SIZE);

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            createHeaderRow(sheet, fieldInfoList, headerStyle);

            int rowIndex = 1;
            for (List<T> batchData : batchDataList) {
                if (batchData != null && !batchData.isEmpty()) {
                    fillDataRowsFromIndex(sheet, fieldInfoList, batchData, dataStyle, rowIndex, workbook);
                    rowIndex += batchData.size();
                }
            }

            for (int i = 0; i < fieldInfoList.size(); i++) {
                int width = fieldInfoList.get(i).getWidth() * 256;
                sheet.setColumnWidth(i, Math.max(width, 3000));
            }

            workbook.write(out);
            out.flush();
        }
    }

    private <T> List<ExcelFieldInfo> resolveExcelFields(Class<T> clazz) {
        List<ExcelFieldInfo> fieldInfoList = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
            if (excelColumn != null && excelColumn.export()) {
                field.setAccessible(true);
                fieldInfoList.add(new ExcelFieldInfo(
                        field,
                        StringUtils.hasText(excelColumn.name()) ? excelColumn.name() : field.getName(),
                        excelColumn.order(),
                        excelColumn.width(),
                        excelColumn.dateFormat(),
                        excelColumn.defaultValue()
                ));
            }
        }

        fieldInfoList.sort(Comparator.comparingInt(ExcelFieldInfo::getOrder));
        return fieldInfoList;
    }

    private void createHeaderRow(Sheet sheet, List<ExcelFieldInfo> fieldInfoList, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);
        headerRow.setHeightInPoints(25);
        for (int i = 0; i < fieldInfoList.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(fieldInfoList.get(i).getColumnName());
            cell.setCellStyle(headerStyle);
        }
    }

    private <T> void fillDataRows(Sheet sheet, List<ExcelFieldInfo> fieldInfoList,
                                  List<T> dataList, CellStyle dataStyle, Workbook workbook) {
        fillDataRowsFromIndex(sheet, fieldInfoList, dataList, dataStyle, 1, workbook);
    }

    private <T> void fillDataRowsFromIndex(Sheet sheet, List<ExcelFieldInfo> fieldInfoList,
                                           List<T> dataList, CellStyle dataStyle, int startRow, Workbook workbook) {
        DataFormat dataFormat = workbook.createDataFormat();
        for (int i = 0; i < dataList.size(); i++) {
            Row dataRow = sheet.createRow(startRow + i);
            dataRow.setHeightInPoints(20);
            T data = dataList.get(i);
            for (int j = 0; j < fieldInfoList.size(); j++) {
                Cell cell = dataRow.createCell(j);
                ExcelFieldInfo fieldInfo = fieldInfoList.get(j);
                Object value = getFieldValue(fieldInfo, data);

                if (value == null) {
                    cell.setCellValue(fieldInfo.getDefaultValue());
                } else if (value instanceof Number) {
                    cell.setCellValue(((Number) value).doubleValue());
                } else if (value instanceof Date) {
                    CellStyle dateStyle = workbook.createCellStyle();
                    dateStyle.cloneStyleFrom(dataStyle);
                    String datePattern = StringUtils.hasText(fieldInfo.getDateFormat())
                            ? fieldInfo.getDateFormat() : "yyyy-MM-dd HH:mm:ss";
                    dateStyle.setDataFormat(dataFormat.getFormat(datePattern));
                    cell.setCellValue((Date) value);
                    cell.setCellStyle(dateStyle);
                    continue;
                } else if (value instanceof Boolean) {
                    cell.setCellValue((Boolean) value);
                } else {
                    cell.setCellValue(value.toString());
                }
                cell.setCellStyle(dataStyle);
            }
        }
    }

    private <T> Object getFieldValue(ExcelFieldInfo fieldInfo, T data) {
        try {
            Object value = fieldInfo.getField().get(data);
            if (value == null && StringUtils.hasText(fieldInfo.getDefaultValue())) {
                return null;
            }
            return value;
        } catch (IllegalAccessException e) {
            log.error("获取字段值失败: {}", fieldInfo.getField().getName(), e);
            return fieldInfo.getDefaultValue();
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        style.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        style.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        style.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        style.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        style.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        style.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        style.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        return style;
    }

    private static class ExcelFieldInfo {
        private final Field field;
        private final String columnName;
        private final int order;
        private final int width;
        private final String dateFormat;
        private final String defaultValue;

        public ExcelFieldInfo(Field field, String columnName, int order, int width,
                              String dateFormat, String defaultValue) {
            this.field = field;
            this.columnName = columnName;
            this.order = order;
            this.width = width;
            this.dateFormat = dateFormat;
            this.defaultValue = defaultValue;
        }

        public Field getField() { return field; }
        public String getColumnName() { return columnName; }
        public int getOrder() { return order; }
        public int getWidth() { return width; }
        public String getDateFormat() { return dateFormat; }
        public String getDefaultValue() { return defaultValue; }
    }
}
