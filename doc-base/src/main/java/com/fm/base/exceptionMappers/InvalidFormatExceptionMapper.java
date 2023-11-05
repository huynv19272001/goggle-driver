package com.fm.base.exceptionMappers;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class InvalidFormatExceptionMapper implements ExceptionMapper<InvalidFormatException> {
    public InvalidFormatExceptionMapper() {}

    public Response toResponse(com.fasterxml.jackson.databind.exc.InvalidFormatException exception) {
        String path = Utils.getFieldPath(exception.getPath());
        String targetType = exception.getTargetType().getSimpleName();
        String actualType = exception.getValue().getClass().getSimpleName();

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(
                    new JsonExceptionResponse(exception.getClass().getSimpleName(), path)
                        .withInfo("expected_type", targetType)
                        .withInfo("actual_type", actualType)
                )
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}