package com.fm.api.helper;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.fm.base.models.dto.Excel;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;

public class ExcelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Excel> listExcels;
    public ExcelExporter(List<Excel> listExcels) {
        this.listExcels = listExcels;
        workbook = new  XSSFWorkbook();
    }


    private void writeHeaderLine(DateTime fromDate, DateTime toDate) {
        DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        String from=dateFormatter.format(fromDate.toDate());
        String to=dateFormatter.format(toDate.toDate());
        sheet = workbook.createSheet("Don Hang");


        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));

        Row row = sheet.createRow(1);
        Row rowHeader = sheet.createRow(0);
        rowHeader.setHeight((short) 500);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);

        CellStyle styleHeader = workbook.createCellStyle();
        XSSFFont fontHeader = workbook.createFont();
        fontHeader.setBold(true);
        fontHeader.setFontHeight(14);
        styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        styleHeader.setAlignment(HorizontalAlignment.CENTER);
        styleHeader.setFont(fontHeader);

        createCell(rowHeader, 0, "Đơn hàng từ ngày "+ from+" đến ngày "+ to, styleHeader);
        createCell(row, 0, "No", style);
        createCell(row, 1, "Tên dự án", style);
        createCell(row, 2, "Mã đơn hàng", style);
        createCell(row, 3, "Trạng Thái", style);
        createCell(row, 4, "Thời gian nhận", style);
        createCell(row, 5, "Thời gian chuyển", style);
        createCell(row, 6, "Giá (VNĐ)", style);
        createCell(row, 7, "Số HĐ khách hàng", style);
        createCell(row,8,"note",style);

    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        }else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        }else if (value instanceof Enum) {
            cell.setCellValue(String.valueOf(value));
        }else{
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 2;
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.RIGHT);
        for (Excel excel : listExcels) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            int columnAuto=0;
            createCell(row, columnCount++, rowCount-2, style);
            createCell(row, columnCount++, excel.getProjectName(), style);
            createCell(row, columnCount++, excel.getOrderCode(),style);
            createCell(row, columnCount++, excel.getStatus(), style);
            createCell(row, columnCount++, excel.getCreatedAt(), style);
            createCell(row, columnCount++, excel.getTransferTime(), style);
            createCell(row, columnCount++, excel.getPrice(), style);
            createCell(row, columnCount++, excel.getOrderId(), style);
            createCell(row, columnCount++, excel.getNote(), style);
        }
        for (int i = 0; i < 9; i++) {
            sheet.autoSizeColumn(i);
        }

    }

    public void export(HttpServletResponse response,DateTime fromDate, DateTime toDate) throws IOException {
        writeHeaderLine(fromDate,toDate);
        writeDataLines();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.close();
        workbook.close();

    }
}
