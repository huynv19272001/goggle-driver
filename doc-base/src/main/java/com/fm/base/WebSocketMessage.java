package com.fm.base;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true) @Data @AllArgsConstructor
public class WebSocketMessage {
    private List<String> topics;
    private String message;
    private String type;
}
