package com.fm.base.exceptionMappers;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class JsonExceptionResponse {
    @JsonProperty("error")
    public String error = "JsonProcessingException";

    @JsonProperty("error_type")
    public String errorType;

    @JsonProperty("field")
    public final String field;

    @JsonProperty("extra_info")
    public final Map<String, Object> extraInfo = new HashMap<String,Object>();

    public JsonExceptionResponse(final String errorType, final String field) {
        this.errorType = errorType;
        this.field = field;
    }

    public JsonExceptionResponse withInfo(String name, Object value) {
        extraInfo.put(name, value);
        return this;
    }
}
