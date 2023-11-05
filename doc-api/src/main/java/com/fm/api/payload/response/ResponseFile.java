package com.fm.api.payload.response;

import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@With
@Builder
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResponseFile extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private Integer status;
    private String message;
    private Object data;

    public ResponseFile(String message) {
        super(message);
    }
}
