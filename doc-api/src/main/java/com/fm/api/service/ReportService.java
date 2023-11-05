package com.fm.api.service;

import com.fm.api.error.ErrorMessage;
import com.fm.base.models.dto.Excel;
import com.fm.base.models.dto.FilterAndTotal;
import com.fm.base.models.dto.SummaryAndCount;
import com.fm.base.models.sql.BaseModel;
import com.fm.base.models.sql.Order;
import com.fm.base.models.sql.Project;
import com.fm.base.repository.sql.OrderRepository;
import com.fm.base.repository.sql.ProjectRepository;
import com.fm.base.repository.sql.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static com.fm.api.error.ErrorMessage.*;

@Service
@AllArgsConstructor
@Slf4j
public class ReportService {
    final private OrderRepository orderDAO;

    private UserRepository userDAO;

    final private ProjectRepository projectDAO;

    public double getTotalPrice() {
        return orderDAO.totalPrice();
    }

    public int totalOrder() {
        return orderDAO.totalOrder();
    }

    public SummaryAndCount totalOrderAndRevenue() {
        return orderDAO.totalOrderAndRevenue();
    }

    public int totalPartner() {
        return orderDAO.totalPartner();
    }

    public FilterAndTotal filterByUserAndDay(Integer userId, String fromDate, String toDate) {
        DateTime from = null;
        DateTime to = null;
        if (StringUtils.isNotBlank(fromDate) && StringUtils.isNotBlank(toDate)) {
            from = new DateTime(fromDate);
            to = new DateTime(toDate).plusHours(23).plusMinutes(59).plusSeconds(59);
            if (from.isAfter(to)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_INPUT_DATE);
            }
        }
        List<Integer> projectIds = new ArrayList<>();
        if (userId != null) {
            if (userDAO.findById(userId).isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.USER_ID_NOT_EXISTS);
            }
            projectIds = projectDAO.listUserProjectIds(userId);
            if(projectIds.isEmpty()){
                projectIds.add(-999999999);
            }
        }
         return new FilterAndTotal(orderDAO.sumAndCountDayByDay(projectIds, from, to));
    }


    public List<Excel> listOrderInExcel(Integer userId, DateTime fromDate, DateTime toDate) {
        List<Excel> excels = new ArrayList<>();
        if (userId != null) {
            if (userDAO.findById(userId).isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.USER_ID_NOT_EXISTS);
            }
        }
        List<Project> projects = projectDAO.listProjectId(userId);
        if (projects == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, HAVE_NOT_PROJECT);
        }
        List<Order> orders = new ArrayList<>();
        for (Project project : projects) {
            orders.addAll(orderDAO.excelExportListOrder(project.getId(), fromDate, toDate.plusDays(1)));
        }
        for (Order order : orders) {
            if (order.getStatus() == Order.Status.DELIVERED) {
                DateTime createDate = order.getCreatedAt().plusHours(7);
                DateTime transferDate = order.getTransferTime().plusHours(7);
                int numberOfDays = convertDateTimeToString(toDate).compareTo(convertDateTimeToString(createDate));
                if (numberOfDays >= 0) {
                    Excel excel = new Excel(order.getProjectName(), order.getOrderCode(), order.getStatus().toString(), createDate, transferDate, order.getPrice(), order.getId(),order.getNote());
                    excels.add(excel);
                }
            }
        }
        return excels;
    }

    public String convertDateTimeToString(DateTime date) {
        return date.toString("yyyy-MM-dd");
    }

}
