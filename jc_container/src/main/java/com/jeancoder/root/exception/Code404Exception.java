package com.jeancoder.root.exception;

@SuppressWarnings("serial")
public class Code404Exception extends RunningException {

	public Code404Exception(String id, String app, String res, String path, String msg, Throwable cause) {
		super(id, app, res, path, msg, cause);
	}
}
