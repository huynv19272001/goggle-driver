package com.fm.base.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@With
public class FilterAndTotal {
    private List<ReportByDay> reportByDays;

    public double getTotalRevenue() {
        if (CollectionUtils.isEmpty(reportByDays)) return 0;
        double totalRevenue = 0;
        for (ReportByDay report : reportByDays) {
            totalRevenue += report.getTotalRevenue();
        }
        return totalRevenue;
    }

    public long getTotalOrder() {
        if (CollectionUtils.isEmpty(reportByDays)) return 0;
        long totalOrder = 0;
        for (ReportByDay report : reportByDays) {
            totalOrder += report.getTotalOrder();
        }
        return totalOrder;
    }


}
