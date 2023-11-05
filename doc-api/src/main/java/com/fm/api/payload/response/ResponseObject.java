package com.fm.api.payload.response;

import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@With
@Builder
public class ResponseObject {
    private Integer status;
    private String message;
    private Object data;
    private static final String SUCCESS = "Successfully";
    private static final String FAILED = "Failed";
    private static final String NOT_FOUND = "Not Found";

    public ResponseObject(HttpStatus status, String message, Object data) {
        this.status = status.value();
        this.message = message;
        this.data = data;
    }

    public ResponseObject(HttpStatus status, String message) {
        this.status = status.value();
        this.message = message;
    }

    public static ResponseEntity<ResponseObject> success() {
        return ResponseEntity.ok().body(new ResponseObject(HttpStatus.OK, SUCCESS));
    }

    public static ResponseEntity<ResponseObject> success(Object data) {
        return ResponseEntity.ok().body(new ResponseObject(HttpStatus.OK, SUCCESS, data));
    }

    public static ResponseEntity<ResponseObject> success(String message) {
        return ResponseEntity.ok().body(new ResponseObject(HttpStatus.OK, message));
    }

    public static ResponseEntity<ResponseObject> success(Object data, String message) {
        return ResponseEntity.ok().body(new ResponseObject(HttpStatus.OK, message, data));
    }

    public static ResponseEntity<ResponseObject> createSuccess(Object data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject(HttpStatus.CREATED, SUCCESS, data));
    }

    public static ResponseEntity<ResponseObject> badRequest() {
        return ResponseEntity.badRequest().body(new ResponseObject(HttpStatus.BAD_REQUEST, FAILED));
    }

    public static ResponseEntity<ResponseObject> badRequest(String message) {
        return ResponseEntity.badRequest().body(new ResponseObject(HttpStatus.BAD_REQUEST, message));
    }

    public static ResponseEntity<ResponseObject> notFound() {
        return ResponseEntity.badRequest().body(new ResponseObject(HttpStatus.NOT_FOUND, NOT_FOUND));
    }

    public static ResponseEntity<ResponseObject> notFound(String message) {
        return ResponseEntity.badRequest().body(new ResponseObject(HttpStatus.NOT_FOUND,message ));
    }
    public static ResponseEntity<ResponseObject> build(HttpStatus status, String message, Object data) {
        return ResponseEntity.status(status).body(new ResponseObject(status, message, data));
    }
}
