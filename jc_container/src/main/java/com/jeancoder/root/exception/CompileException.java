package com.jeancoder.root.exception;

@SuppressWarnings("serial")
public class CompileException extends RunningException {

	public CompileException(String id, String app, String msg, Throwable cause) {
		super(id, app, msg, cause);
	}
	
	public CompileException(String id, String app, String path, String msg, Throwable cause) {
		super(id, app, path, msg, cause);
	}
}
