package com.jeancoder.core.exception;

/**
 * SDK 运行时报的异常
 * @author huangjie
 *
 */
@SuppressWarnings("serial")
public class SdkRuntimeException extends AppException {
	
	public SdkRuntimeException() {
        super();
    }

    public SdkRuntimeException(String message) {
        super(message);
    }

    public SdkRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
