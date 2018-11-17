package com.jeancoder.core.exception;

@SuppressWarnings("serial")
public class AppLoadException extends AppException {
	
	public AppLoadException() {
        super();
    }

    public AppLoadException(String message) {
        super(message);
    }

    public AppLoadException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
