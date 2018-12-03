package com.jeancoder.root.exception;

@SuppressWarnings("serial")
public class SPPEmptyException extends RunningException {

	public SPPEmptyException(String id, String app, String res, String path, String msg, Throwable cause) {
		super(id, app, res, path, msg, cause);
	}
	
}
