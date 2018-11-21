package com.jeancoder.root.exception;

@SuppressWarnings("serial")
public class Code404Exception extends RunningException {

	public Code404Exception(String id, String app, String msg, Throwable cause) {
		super(id, app, msg, cause);
	}
	
	public Code404Exception(String id, String app, String path, String msg, Throwable cause) {
		super(id, app, path, msg, cause);
	}
}
