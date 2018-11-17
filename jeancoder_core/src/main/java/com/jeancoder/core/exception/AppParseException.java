package com.jeancoder.core.exception;

/**
 * App  转换成资源的时候产生的 异常
 * @author huangjie
 *
 */
@SuppressWarnings("serial")
public class AppParseException extends AppException {

	public AppParseException() {
        super();
    }

    public AppParseException(String message) {
        super(message);
    }

    public AppParseException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
