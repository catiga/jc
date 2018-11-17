package com.jeancoder.core.exception;


/**
 * App 获取时产生的异常
 * @author huangjie
 *
 */
@SuppressWarnings("serial")
public class AppFetchException extends AppException{
	public AppFetchException() {
        super();
    }

    public AppFetchException(String message) {
        super(message);
    }

    public AppFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}
