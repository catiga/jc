package com.jeancoder.core.exception;

/**
 * 运行时报的异常
 * @author huangjie
 *
 */
@SuppressWarnings("serial")
public class AppRunnerException extends AppException{
	
	public AppRunnerException() {
        super();
    }

    public AppRunnerException(String message) {
        super(message);
    }

    public AppRunnerException(String message, Throwable cause) {
        super(message, cause);
    }
}
