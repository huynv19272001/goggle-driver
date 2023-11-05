package com.fm.api.payload.response;

import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@With
@Builder
public class MediaDBResponse {


    private String url;
    private String originalName;
    private String mimeType;
    private String type;
    public static ResponseEntity<?> createSuccess(Object data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseObject.builder().status(200).message("Successfully").data(data).build());
    }
    public static ResponseEntity<?> badRequest() {
        return ResponseEntity.badRequest().body(ResponseObject.builder().status(400).message("Failed").build());
    }
}
