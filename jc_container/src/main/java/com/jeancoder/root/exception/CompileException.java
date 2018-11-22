package com.jeancoder.root.exception;

@SuppressWarnings("serial")
public class CompileException extends RunningException {

	public CompileException(String id, String app, String res, String path, String msg, Throwable cause) {
		super(id, app, res, path, msg, cause);
	}
}
