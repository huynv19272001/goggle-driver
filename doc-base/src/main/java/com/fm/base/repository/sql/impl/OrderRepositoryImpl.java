package com.fm.base.repository.sql.impl;

import com.fm.base.models.dto.ReportByDay;
import com.fm.base.models.sql.Order;
import com.fm.base.repository.sql.custom.OrderCustomRepository;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DoubleType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

public class OrderRepositoryImpl implements OrderCustomRepository {
    @Autowired
    EntityManager entityManager;

    @Override
    public Page<Order> listOrderWithFindCondition(String customerName, String orderCode, String status, String fromDate, String toDate, Pageable pageable) {
        String query = "select * from orders o where true ";
        String countQuery = "select count(*) from orders o where true ";
        Map<String, Object> filters = new HashMap<>();

        if (StringUtils.isNotBlank(orderCode)) {
            query += "  and lower(o.order_code) like lower(concat('%', concat(:orderCode, '%'))) ";
            countQuery += " and lower(o.order_code) like lower(concat('%', concat(:orderCode, '%'))) ";
            filters.put("orderCode", orderCode);
        }
        if (StringUtils.isNotBlank(customerName)) {
            query += " and o.customer_name =:customerName ";
            countQuery += " and o.customer_name =:customerName ";
            filters.put("customerName", customerName);
        }
        if (StringUtils.isNotBlank(status)) {
            query += " and o.status =:status ";
            countQuery += " and o.status =:status ";
            filters.put("status", status);
        }
        if (StringUtils.isNotBlank(fromDate) && StringUtils.isNotBlank(toDate)) {
            Date from = new DateTime(fromDate).toDate();
            Date to = new DateTime(toDate).plusDays(1).toDate();
            query += " and (o.receive_time between :from and :to) ";
            countQuery += " and (o.receive_time between :from and :to) ";
            filters.put("from", from);
            filters.put("to", to);
        }
        query += " order  by id desc " ;
        Query createQuery = entityManager.createNativeQuery(query, Order.class)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());
        Query createCountQuery = entityManager.createNativeQuery(countQuery);
        filters.forEach((k, v) -> {
            createQuery.setParameter(k, v);
            createCountQuery.setParameter(k, v);
        });
        List<Order> resultList = createQuery.getResultList();
        long count = ((Number) createCountQuery.getSingleResult()).longValue();
        return new PageImpl<>(resultList, pageable, count);
    }

    @Override
    public List<ReportByDay> sumAndCountDayByDay(List<Integer> projectIds, DateTime fromDate, DateTime toDate) {
        StringBuilder sql = new StringBuilder();
        sql.append("select to_char(o.transfer_time, 'DD/MM/YYYY') date, sum(o.price) totalRevenue, count(o) totalOrder from orders o where o.status ='DELIVERED'");
        if (fromDate != null && toDate != null) {
            sql.append(" and o.transfer_time between '").append(fromDate).append("' and '").append(toDate).append(" '");
        }

        if (!projectIds.isEmpty()) {
            String projectIdString = StringUtils.join(projectIds, ",");
            sql.append(" and o.project_id in ( ").append((projectIdString)).append(")");
        }
        sql.append(" GROUP BY to_char(o.transfer_time, 'DD/MM/YYYY')");
        sql.append(" ORDER BY to_char(o.transfer_time, 'DD/MM/YYYY') ");

        List<ReportByDay> listResult = entityManager.unwrap(org.hibernate.Session.class).createNativeQuery(sql.toString())
                .addScalar("date", StringType.INSTANCE)
                .addScalar("totalRevenue", DoubleType.INSTANCE)
                .addScalar("totalOrder", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(ReportByDay.class)).list();
        return listResult;
    }

//    @Override
//    public List<ReportByDay> reportByUser(List<Integer> projectIds) {
//        StringBuilder sql = new StringBuilder();
//        String projectIdString =  StringUtils.join(projectIds,",");
//        sql.append("select to_char(o.transfer_time, 'DD/MM/YYYY') date, sum(o.price) totalRevenue, count(o) totalOrder from orders o where o.status ='DELIVERED' and  ");
//        sql.append("project_id in ( ").append((projectIdString)).append(") GROUP BY to_char(o.transfer_time, 'DD/MM/YYYY') ORDER BY to_char(o.transfer_time, 'DD/MM/YYYY') ");
//        List<ReportByDay> listResult = entityManager.unwrap(org.hibernate.Session.class).createNativeQuery(sql.toString())
//                .addScalar("date", StringType.INSTANCE)
//                .addScalar("totalRevenue", DoubleType.INSTANCE)
//                .addScalar("totalOrder", LongType.INSTANCE)
//                .setResultTransformer(Transformers.aliasToBean(ReportByDay.class)).list();
//        return listResult;
//    }

    @Override
    public Page<Order> listOrderProjectByStatus(Integer projectId, List<Integer> projectIds, List<String> status, Pageable pageable) {
        String query = "select * from orders o inner join projects p on o.project_id = p.id where true ";
        String countQuery = "select count(*) from orders o inner join projects p on o.project_id = p.id where true ";
        Map<String, Object> filters = new HashMap<>();
        if (projectId != null) {
            query += " and p.id =:projectId ";
            countQuery += " and p.id =:projectId ";
            filters.put("projectId", projectId);
        }
        if (projectIds != null) {
            query += " and o.project_id not in (:projectIds)";
            countQuery += " and o.project_id not in (:projectIds)";
            filters.put("projectIds", projectIds);
        }
        if (status != null) {
            query += " and o.status in (:status)";
            countQuery += " and o.status in (:status)";
            filters.put("status", status);
        }
        Query createQuery = entityManager.createNativeQuery(query, Order.class)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());
        Query createCountQuery = entityManager.createNativeQuery(countQuery, Integer.class);
        filters.forEach(createQuery::setParameter);
        List<Order> resultList = createQuery.getResultList();
        int count = createCountQuery.getFirstResult();
        return new PageImpl<>(resultList, pageable, count);
    }


}
