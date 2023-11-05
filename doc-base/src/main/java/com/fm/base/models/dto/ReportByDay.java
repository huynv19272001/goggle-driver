package com.fm.base.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportByDay {
    private String date;
    private double totalRevenue;
    private long totalOrder;


//    public String getDate() {
//        return Optional.ofNullable(date).map(d -> d.toString("dd/MM/yyyy")).orElse("");
//    }
}
