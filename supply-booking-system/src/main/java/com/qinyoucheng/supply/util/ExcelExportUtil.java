package com.qinyoucheng.supply.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ExcelExportUtil {

    public static void export(HttpServletResponse response, String sheetName,
                             String[] headers, String[] fields,
                             List<Map<String, Object>> dataList) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);

            CellStyle headerStyle = createHeaderStyle(workbook);
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 5000);
            }

            CellStyle dataStyle = createDataStyle(workbook);
            for (int i = 0; i < dataList.size(); i++) {
                Row row = sheet.createRow(i + 1);
                Map<String, Object> data = dataList.get(i);
                for (int j = 0; j < fields.length; j++) {
                    Cell cell = row.createCell(j);
                    Object value = data.get(fields[j]);
                    cell.setCellValue(value != null ? value.toString() : "");
                    cell.setCellStyle(dataStyle);
                }
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + java.net.URLEncoder.encode(sheetName + ".xlsx", "UTF-8"));

            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException("导出Excel失败", e);
        }
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        return style;
    }

    private static CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        return style;
    }
}
