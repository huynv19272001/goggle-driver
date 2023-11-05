package com.fm.api.service;

import com.fm.api.utils.ListResult;
import com.fm.base.models.sql.Order;
import com.fm.base.models.sql.Project;
import com.fm.base.repository.sql.OrderRepository;
import com.fm.base.repository.sql.ProjectRepository;
import com.fm.base.utils.PageableUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class DashboardService {
    private final OrderRepository orderRepository;
    private final ProjectRepository projectRepository;

    public List<Project> findProjectShow() {
        return projectRepository.findProjectShow(String.valueOf(Project.Dashboard.SHOW));
    }

    public ListResult<Order> listOrderProjectByStatus(Integer projectId, List<String> status,  int page, int size, String orderBy, boolean desc) {
        List<Integer> projectIds =null;
        if(projectId == null){
             projectIds = projectRepository.findProjectShow(String.valueOf(Project.Dashboard.SHOW))
                    .stream().map(Project::getId).collect(Collectors.toList());
        }
        return ListResult.from(orderRepository.listOrderProjectByStatus(projectId,projectIds, status,  PageableUtils.pageable(page, size, orderBy, desc)));
    }

    public List<Order> findAllOrders(){
        return orderRepository.findAll();
    }
}
