package com.fm.api.controller;

import com.fm.api.payload.response.ResponseObject;
import com.fm.api.service.DashboardService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping({"/dashboard"})
@AllArgsConstructor
public class DashboardController {
    private DashboardService dashboardService;

    @GetMapping("/project-show")
    public ResponseEntity<?> findProjectShow(){
        try {
            return ResponseObject.success(dashboardService.findProjectShow());
        } catch (Exception e) {
            return ResponseObject.badRequest();
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<?> findAllOrdersByStatus(@RequestParam(value = "projectId", required = false) Integer projectId,   //project show dashboard
                                                   @RequestParam(value = "status", required = false) List<String> status,
                                                   @RequestParam(value = "page", defaultValue = "1") int page,
                                                   @RequestParam(value = "size", defaultValue = "20") int size,
                                                   @RequestParam(value = "orderBy", defaultValue = "createAt") String orderBy,
                                                   @RequestParam(value = "desc", defaultValue = "false") boolean desc) {
        try {
            return ResponseObject.success(dashboardService.listOrderProjectByStatus(projectId, status,page,size,orderBy,desc));
        } catch (Exception e) {
            return ResponseObject.badRequest();
        }
    }

    @GetMapping("/all-orders")
    public ResponseEntity<?> findAllOrders(){
        try {
            return ResponseObject.success(dashboardService.findAllOrders());
        } catch (Exception e) {
            return ResponseObject.badRequest();
        }
    }
}
