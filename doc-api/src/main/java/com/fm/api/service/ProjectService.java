
package com.fm.api.service;

import com.fm.api.error.ErrorMessage;
import com.fm.api.payload.response.ProjectResponse;
import com.fm.api.payload.response.UserResponse;
import com.fm.api.utils.ListResult;
import com.fm.base.models.sql.Project;
import com.fm.base.repository.sql.OrderRepository;
import com.fm.base.repository.sql.ProjectRepository;
import com.fm.base.repository.sql.UserRepository;
import com.fm.base.utils.PageableUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.fm.base.utils.PageableUtils.pageable;

@AllArgsConstructor
@Service
@Slf4j
public class ProjectService {
    private final ProjectRepository projectDAO;

    private final UserRepository userDAO;

    private final OrderRepository orderDAO;

    public List<ProjectResponse> getAll() {
        Map<Integer, Long> numberOrders = getMapNumberOrder();
        Map<Integer, UserResponse> userResponses = getMapUserResponse();
        return projectDAO.findAll().stream().map(it -> {
            Long numberOrder = numberOrders.get(it.getId());
            return new ProjectResponse(it.getId(), it.getName(), it.getCode(), numberOrder == null ? 0 : numberOrder, it.getDashboard().toString(), userResponses.get(it.getUserId()));
        }).collect(Collectors.toList());
    }

    public ListResult<ProjectResponse> filter(String name, String code, String keyWord, int page, int size, String orderBy, boolean desc) {
        Map<Integer, Long> numberOrders = getMapNumberOrder();
        Map<Integer, UserResponse> userResponses = getMapUserResponse();
        Page<ProjectResponse> projectResponsePage = projectDAO.filter(name != null ? name.trim() : null, code != null ? code.trim() : null, keyWord != null ? keyWord.trim() : null, PageableUtils.pageable(page, size, orderBy, desc))
                .map(it -> {
                    Long numberOrder = numberOrders.get(it.getId());
                    return new ProjectResponse(it.getId(), it.getName(), it.getCode(), numberOrder == null ? 0 : numberOrder, it.getDashboard().toString(), userResponses.get(it.getUserId()));
                });
        return ListResult.from(projectResponsePage);
    }

    public Project create(Project project) {
        userDAO.findByIdAndDeletedAtNull(project.getUserId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessage.USER_ID_NOT_EXISTS));
        if (projectDAO.findByNameOrCode(project.getName(), project.getCode()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.NAME_OR_PROJECT_ALREADY_EXISTS);
        }
        return projectDAO.save(project);
    }

    public Project update(Integer id, Project request) {
        Project project = projectDAO.findByIdAndDeletedAtNull(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessage.PROJECT_ID_NOT_EXISTS));
        userDAO.findByIdAndDeletedAtNull(request.getUserId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessage.USER_ID_NOT_EXISTS));
        if (projectDAO.findByIdAndNameOrCode(id, request.getName(), request.getCode()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.NAME_OR_PROJECT_ALREADY_EXISTS);
        }
        project.setDashboard(request.getDashboard());
        project.setCode(request.getCode());
        project.setName(request.getName());
        project.setUserId(request.getUserId());
        project.setUpdatedAt(DateTime.now());
        return projectDAO.save(project);
    }

    public Optional<Project> getById(Integer id) {
        return projectDAO.findByIdAndDeletedAtNull(id);
    }

    public boolean delete(Integer id) {
        Optional<Project> employee = projectDAO.findById(id);
        if (employee.isPresent()) {
            projectDAO.deleteById(id);
            return true;
        }
        return false;
    }

    public Map<Integer, Long> getMapNumberOrder() {
        Map<Integer, Long> numberOrders = new HashMap<>();
        orderDAO.groupByProjectId().forEach(it ->
                numberOrders.put(it.getId(), it.getCountOrder()));
        return numberOrders;
    }

    public Map<Integer, UserResponse> getMapUserResponse() {
        Map<Integer, UserResponse> userResponses = new HashMap<>();
        userDAO.findAllByDeletedAtNull().forEach(it -> {
            UserResponse userResponse = new UserResponse(it.getId(), it.getUserName(), it.getName(), it.getPhoneNumber(), it.getEmail());
            userResponses.put(it.getId(), userResponse);
        });
        return userResponses;
    }
}