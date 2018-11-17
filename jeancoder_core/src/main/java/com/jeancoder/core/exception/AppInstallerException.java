package com.jeancoder.core.exception;

/**
 * App 装载时报的异常
 * @author huangjie
 *
 */
@SuppressWarnings("serial")
public class AppInstallerException extends RuntimeException  {
	public AppInstallerException() {
        super();
    }

    public AppInstallerException(String message) {
        super(message);
    }

    public AppInstallerException(String message, Throwable cause) {
        super(message, cause);
    }
}
