package com.fm.base.repository.sql.custom;

import com.fm.base.models.dto.ReportByDay;
import com.fm.base.models.sql.Order;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface OrderCustomRepository {
    Page<Order>listOrderWithFindCondition(String customerName,String orderCode, String status, String fromDate, String toDate, Pageable pageable);

    List<ReportByDay> sumAndCountDayByDay(List<Integer> projectIds,DateTime fromDate, DateTime toDate);

//    List<ReportByDay> reportByUser(List<Integer> projectIds);

    Page<Order> listOrderProjectByStatus(Integer projectId,List<Integer> projectIds, List<String> status, Pageable pageable);
}
