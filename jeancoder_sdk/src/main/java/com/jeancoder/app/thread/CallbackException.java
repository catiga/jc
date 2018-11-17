package com.jeancoder.app.thread;

@SuppressWarnings("serial")
public class CallbackException extends RuntimeException {

	Throwable ex;
	
	String code;
	
	String msg;

	public CallbackException(Throwable ex, String code, String msg) {
		super();
		this.ex = ex;
		this.code = code;
		this.msg = msg;
	}
	
}
