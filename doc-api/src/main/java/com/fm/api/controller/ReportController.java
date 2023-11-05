package com.fm.api.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fm.api.helper.ExcelExporter;
import com.fm.api.payload.response.ResponseObject;
import com.fm.api.service.ReportService;
import com.fm.base.models.dto.Excel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/report")
public class ReportController {
    final private ReportService reportService;

    @GetMapping("/total-revenue")
    public ResponseEntity<?> getRevenueTotal() {
        return ResponseObject.success(reportService.getTotalPrice());
    }

    @GetMapping("/total-order")
    public ResponseEntity<?> getTotalOrder() {
        return ResponseObject.success(reportService.totalOrder());
    }

    @GetMapping("/total-partner")
    public ResponseEntity<?> getTotalPartner() {
        return ResponseObject.success(reportService.totalPartner());
    }

    @GetMapping("/order-and-revenue")
    public ResponseEntity<?> getTotalOrderAndRevenue() {
        return ResponseObject.success(reportService.totalOrderAndRevenue());
    }

    @GetMapping("/by-user-and-days")
    public ResponseEntity<?> getFromDayToDay(@RequestParam(value = "userId", required = false) Integer userId,
                                             @RequestParam(value = "fromDate", required = false) String fromDate,
                                             @RequestParam(value = "toDate", required = false) String toDate) {
        return ResponseObject.success(reportService.filterByUserAndDay(userId, fromDate, toDate));
    }

    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response,
                              @RequestParam(value = "userId",required = false) Integer userId,
                              @RequestParam("fromDate") String fromDate,
                              @RequestParam("toDate") String toDate) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String currentDateTime = dateFormatter.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Don_hang_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);
        List<Excel> listExcels = reportService.listOrderInExcel(userId, new DateTime(fromDate), new DateTime(toDate));
        ExcelExporter excelExporter = new ExcelExporter(listExcels);
        excelExporter.export(response, new DateTime(fromDate), new DateTime(toDate));
    }
}
