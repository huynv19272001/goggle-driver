package com.fm.api.controller;

import com.fm.api.payload.response.ResponseObject;
import com.fm.api.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/notification")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping({"/", "", "/all"})
    public ResponseEntity<?> getAllNotification(Pageable pageable) {
        return ResponseObject.success(notificationService.getAll(pageable));
    }
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateIsRead(@PathVariable Integer id) {
        return  ResponseObject.success(notificationService.updateIsRead(id));
    }

    @PatchMapping("")
    public ResponseEntity<?> updateAllIsRead() {
        return ResponseObject.success(notificationService.updateAllIsRead());
    }

}
