package com.jeancoder.root.exception;

@SuppressWarnings("serial")
public class Code500Exception extends RunningException {

	public Code500Exception(String id, String app, String msg, Throwable cause) {
		super(id, app, msg, cause);
	}
	
	public Code500Exception(String id, String app, String path, String msg, Throwable cause) {
		super(id, app, path, msg, cause);
	}
}
