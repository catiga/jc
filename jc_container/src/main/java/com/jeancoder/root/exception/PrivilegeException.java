package com.jeancoder.root.exception;

@SuppressWarnings("serial")
public class PrivilegeException extends RunningException {

	public PrivilegeException(String id, String app, String res, String path, String msg, Throwable cause) {
		super(id, app, res, path, msg, cause);
	}
}
