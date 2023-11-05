package com.fm.base.exceptionMappers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Collection;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class UnrecognizedFieldExceptionMapper implements ExceptionMapper<UnrecognizedPropertyException> {

    public UnrecognizedFieldExceptionMapper() {
    }

    public Response toResponse(com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException exception) {
        String path = Utils.getFieldPath(exception.getPath());

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(
                    new JsonExceptionResponse(exception.getClass().getSimpleName(), path)
                        .withInfo("known_fields", exception.getKnownPropertyIds())
                )
                .type(MediaType.APPLICATION_JSON)
                .build();
    }


    public class CustomHttpResponse {
        @JsonProperty("unrecognized_property")
        public final String unrecognizedProperty;

        @JsonProperty("known_properties")
        public final Collection<Object> knowProperties;

        public CustomHttpResponse(final String unrecognizedProperty, final Collection<Object> knowProperties) {
            this.unrecognizedProperty = unrecognizedProperty;
            this.knowProperties = knowProperties;
        }
    }
}