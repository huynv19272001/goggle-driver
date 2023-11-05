package com.fm.api.utils;

import com.fm.base.models.sql.Order;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class WriteDataToOrderCSV {
    public static ByteArrayInputStream tutorialsToCSV(List<Order> orderList) {
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {
            for (Order order : orderList) {
                List<? extends Serializable> data = Arrays.asList(
                        order.getId(),
                        order.getProjectName(),
                        order.getCustomerName(),
                        order.getOrderCode(),
                        order.getStatus(),
                        order.getCreatorId(),
                        order.getTransferTime(),
                        order.getPageTotal(),
                        order.getPriceListId(),
                        order.getReceiveTime(),
                        order.getPrintedTime(),
                        order.getPackedTime(),
                        order.getPrice(),
                        order.getIsApproved(),
                        order.getProjectId(),
                        order.getNote(),
                        order.getNumberReprint()
                );
                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to CSV file: " + e.getMessage());
        }
    }
}
