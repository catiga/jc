package com.jeancoder.core.exception;

@SuppressWarnings("serial")
public class AppException extends RuntimeException {
	public AppException() {
        super();
    }

    public AppException(String message) {
        super(message);
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }
}
