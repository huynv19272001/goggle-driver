package com.fm.base.qmessages;

import com.fm.base.utils.Mapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data @SuperBuilder @AllArgsConstructor @NoArgsConstructor
public class BaseMessage {
    @JsonProperty("action") public String action = "";
    @Override
    public String toString() {
        ObjectMapper MAPPER = Mapper.generateStandardObjectMapper();
        try {
            return MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
