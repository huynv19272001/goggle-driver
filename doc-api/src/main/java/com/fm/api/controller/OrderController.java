package com.fm.api.controller;

import com.fm.api.error.ErrorMessage;
import com.fm.api.payload.response.OrderActionResponse;
import com.fm.api.payload.response.ResponseObject;
import com.fm.api.service.FileService;
import com.fm.api.service.OrderService;
import com.fm.base.models.dto.UpdateOrderDTO;
import com.fm.base.models.dto.UpdatePriceNoteOrderDTO;
import com.fm.base.models.dto.UpdateStatusListOrderDTO;
import com.fm.base.models.sql.OrderAction;
import com.fm.base.repository.sql.FileRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping({"/order", "/orders"})
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;

    private final FileService fileService;
    @Autowired
    FileRepository fileRepository;

    @GetMapping({""})
    public ResponseEntity<?> getAllOrder(  @RequestParam(value = "sortBy", defaultValue = "id") String sortBy) {
        return ResponseObject.success(orderService.getAllOrder(sortBy));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable("orderId") Integer orderId) {
        return ResponseObject.success(orderService.getOrder(orderId));
    }


    @GetMapping({"/filter"})
    public ResponseEntity<?> filter(@RequestParam(value = "customerName", required = false) String customerName,
                                    @RequestParam(value = "orderCode", required = false) String orderCode,
                                    @RequestParam(value = "status", required = false) String status,
                                    @RequestParam(value = "startDate", required = false) String startDate,
                                    @RequestParam(value = "endDate", required = false) String endDate,
                                    @RequestParam(value = "page", defaultValue = "1") int page,
                                    @RequestParam(value = "size", defaultValue = "20") int size,
                                    @RequestParam(value = "orderBy", defaultValue = "createAt") String orderBy,
                                    @RequestParam(value = "desc", defaultValue = "false") boolean desc
    ) {
        return ResponseObject.success(orderService.listOrderWithFindCondition(customerName, orderCode, status, startDate, endDate,page,size,orderBy,desc));
    }

    @PatchMapping({"/approve/{orderId}"})
    public ResponseEntity<?> approveOrder(@PathVariable("orderId") Integer orderId) {
        return ResponseObject.success(orderService.approveOrder(orderId));
    }

    @PatchMapping("/update-status")
    public ResponseEntity<?> updateStatus(@RequestParam(name = "order-id") @Min(value = 1, message = ErrorMessage.MINIMUM_VALUE_ORDER_ID) Integer orderId,
                                          @RequestParam(name = "type-action") OrderAction.Type typeAction) {
        OrderActionResponse orderActionResponse = orderService.updateStatus(orderId, typeAction);
        return ResponseObject.success(orderActionResponse);
    }

    @PutMapping("/update-status-orders")
    public ResponseEntity<?> updateStatusOfListOrder(@Valid @RequestBody UpdateStatusListOrderDTO request) {
        return ResponseObject.success(orderService.updateStatusOfListOrder(request));
    }

    @PatchMapping({"/cancel/{orderId}"})
    public ResponseEntity<?> cancelOrder(@PathVariable("orderId") Integer orderId) {
        return ResponseObject.success(orderService.cancelOrder(orderId));
    }

    @PutMapping("/edit-price-order")
    public ResponseEntity<?> editPriceOrder(@RequestBody UpdateOrderDTO updateOrderDTO) {
        return ResponseObject.success(orderService.editPriceOrder(updateOrderDTO));
    }

    @PutMapping("/edit-note-order")
    public ResponseEntity<?> editNoteOrder(@RequestBody UpdateOrderDTO updateOrderDTO) {
        return ResponseObject.success(orderService.editNoteOrder(updateOrderDTO));
    }

    @PostMapping("/edit-order")
    public ResponseEntity<?> editOrder(@RequestParam(value = "multipartFiles", required = false) List<MultipartFile> multipartFiles,
                                       @RequestParam("orderId") Integer orderId,
                                       @RequestParam(value = "fileIds", required = false) List<Integer> fileIds,
                                       @RequestParam(value = "projectId", required = false) Integer projectId,
                                       @RequestParam(value = "priceListId", required = false) Integer priceListId,
                                       @RequestParam(value = "priceOrder", required = false) Integer priceOrder,
                                       @RequestParam(value = "note", required = false) String note) {
        return ResponseObject.success(orderService.editOrder(multipartFiles, orderId, fileIds, projectId, priceListId, priceOrder, note));
    }

    @PostMapping({"/create-order"})
    public ResponseEntity<?> createOrder(@RequestParam(value = "multipartFiles", required = false) List<MultipartFile> multipartFiles,
                                         @RequestParam(value = "priceOrder", required = false) Integer priceOrder,
                                         @RequestParam(value = "projectId", required = false) Integer projectId,
                                         @RequestParam(value = "priceListId", required = false) Integer priceListId,
                                         @RequestParam(value = "orderCode", required = false) String orderCode,
                                         @RequestParam(value = "note", required = false) String note) {
        return ResponseObject.createSuccess(orderService.createOrder(multipartFiles, priceOrder, projectId, priceListId == null ? 0 : priceListId, orderCode, note));

    }

    @PatchMapping("/update/{orderId}")
    public ResponseEntity<?> updateNoteStatusPrice(@PathVariable(value = "orderId", required = false) Integer orderId,
                                                   @RequestBody UpdatePriceNoteOrderDTO updatePriceNoteOrder) {
        return ResponseObject.success(orderService.updatePriceNoteStatus(orderId, updatePriceNoteOrder));
    }


    @GetMapping("/download-file")
    public ResponseEntity<?> downloadFile(@RequestParam String fileName) {
        return ResponseEntity
                .ok()
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(fileService.downloadFile(fileName).toByteArray());
    }


    @PatchMapping({"/update-order/{id}"})
    public ResponseEntity<?> updateOrder(@PathVariable("id") Integer id,
                                         @RequestBody UpdateOrderDTO updateOrderDTO) {

        return ResponseObject.success(orderService.updateOrder(id, updateOrderDTO.getCustomerName()));
    }


    @GetMapping({"/list-order-by-user-login"})
    public ResponseEntity<?> listOrderByUserLogin(@RequestParam(value = "page", defaultValue = "1") int page,
                                                  @RequestParam(value = "size", defaultValue = "10") int size,
                                                  @RequestParam(value = "orderBy", defaultValue = "id") String orderBy,
                                                  @RequestParam(value = "desc", defaultValue = "false") boolean desc) {
        return ResponseObject.success(orderService.getOrdersByUser(page, size, orderBy, desc));
    }

    @GetMapping("/downloadOrderCSV")
    public ResponseEntity<?> getFile(@RequestParam("fileName") String fileName) {
        return  ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(new InputStreamResource(orderService.load()));
    }

}

