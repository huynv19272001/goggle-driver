package com.fm.base.message.core.exceptions;

public class ShutdownCauseException extends RuntimeException {
    public ShutdownCauseException(String message) { super(message); }
    public ShutdownCauseException(String message, Throwable throwable) { super(message, throwable); }
}
