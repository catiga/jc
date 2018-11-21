package com.jeancoder.root.exception;

@SuppressWarnings("serial")
public class RunningException extends RuntimeException {

	String id;
	
	String app;
	
	String path;

	public RunningException(String id, String app, String msg, Throwable cause) {
		super(msg, cause);
		this.id = id;
		this.app = app;
	}
	
	public RunningException(String id, String app, String path, String msg, Throwable cause) {
		super(msg, cause);
		this.id = id;
		this.app = app;
		this.path = path;
	}
	
}
