package com.fm.base.exceptionMappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class CustomJsonExceptionMapper extends JsonProcessingExceptionMapper {
    private static final Logger LOG = LoggerFactory.getLogger(UnrecognizedFieldExceptionMapper.class);

    public CustomJsonExceptionMapper() {}

    @Override
    public Response toResponse(JsonProcessingException exception) {
        log.error("{}: {}", exception.getClass().getCanonicalName(), exception.getMessage());

        if (exception instanceof JsonMappingException) {
            JsonMappingException mappingException = (JsonMappingException) exception;
            if (mappingException.getPath().size() > 0) {
                Map<String, List<String>> response = new HashMap<>();
                List<String> errors = new ArrayList<>();
                errors.add(mappingException.getPath().get(0).getFieldName() + " " + exception.getOriginalMessage());
                response.put("errors", errors);
                return Response.status(422)
                        .entity(
                                response
                        )
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
        }

        return super.toResponse(exception);
    }
}