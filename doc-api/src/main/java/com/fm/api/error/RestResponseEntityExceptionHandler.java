package com.fm.api.error;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fm.api.payload.response.ResponseObject;
import com.fm.api.security.jwt.exception.TokenRefreshException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler {


    @ExceptionHandler( IllegalArgumentException.class)
    public ResponseEntity<?> handleInternal(IllegalArgumentException ex) {
        return ResponseObject.build(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    //TokenRefreshException
    @ExceptionHandler({TokenRefreshException.class})
    public ResponseEntity<?> handleTokenRefreshException(final RuntimeException ex, final WebRequest request) {
        return ResponseObject.build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage(), null);
    }

    //404
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> handleNoHandlerFound(NoHandlerFoundException ex, WebRequest request) {
        ex.printStackTrace();
        return ResponseObject.build(HttpStatus.NOT_FOUND, ex.getLocalizedMessage(), null);
    }

    //400
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleStatusException(ResponseStatusException ex, WebRequest request) {
        return ResponseObject.build(ex.getStatus(), ex.getReason(), null);
    }

    @ExceptionHandler({InvalidFormatException.class})
    public ResponseEntity<?> handleInvalidFormatException(InvalidFormatException ex) {
        return ResponseObject.build(HttpStatus.BAD_REQUEST, String.format("'%s' is not valid for field '%s'", ex.getValue(), ex.getPath().get(0).getFieldName()), null);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return ResponseObject.build(HttpStatus.BAD_REQUEST, ex.getBindingResult().getAllErrors().get(0).getDefaultMessage(), null);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<?> handleClientErrorException(HttpClientErrorException ex) {
        return ResponseObject.build(ex.getStatusCode(), ex.getLocalizedMessage(), null);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException ex) {
        return ResponseObject.build(HttpStatus.BAD_REQUEST, ex.getMessage().split(":")[1], null);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingServletRequestPartException(MissingServletRequestParameterException ex) {
        return ResponseObject.build(HttpStatus.BAD_REQUEST, String.format("Parameter name '%s' has data type '%s' missing", ex.getParameterName(), ex.getParameterType()), null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String description;
        Class<?> requiredType = ex.getRequiredType();
        if (requiredType != null) {
            description = "Invalid " + ex.getName() + " require '" + requiredType.getSimpleName() + "' type";
        } else {
            description = ex.getMessage();
        }
        return ResponseObject.build(HttpStatus.BAD_REQUEST, description, null);
    }

    @ExceptionHandler(JsonMappingException.class)
    public ResponseEntity<?> handleJsonMappingException(JsonMappingException ex) {
        return ResponseObject.build(HttpStatus.BAD_REQUEST,ex.getPath().get(0).getFieldName() + " Invalid format date time", null);
    }

}
