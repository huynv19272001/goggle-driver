package com.fm.base.utils;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
public class DocfmResponse {
    public static Response ok(Object entity){
        return Response.status(Status.OK).entity(entity).build();
    }

    public static Response created(Object entity){
        return Response.status(Status.CREATED).entity(entity).build();
    }

    public static Response buildErr(final Response.Status responseStatus, final String message, final String messageMy) {
        return buildRes(responseStatus, message, messageMy).build();
    }

    public static ResponseBuilder buildRes(final Status responseStatus, final String message, final String messageMy) {
        return Response.status(responseStatus)
                .entity(new DocfmErrEntity(responseStatus.getStatusCode(), message, messageMy));
    }

    public static ClientErrorException buildException(final Status responseStatus, final String message, final String messageMy) {
        return new ClientErrorException(buildRes(responseStatus, message, messageMy).build());
    }

    public static class DocfmErrEntity {
        public Integer code;
        public String message;
        public String messageMy;


        public DocfmErrEntity(final Integer code, final String message, final String messageMy) {
            this.code = code;
            this.message = message;
            this.messageMy = messageMy;
        }
    }

}
