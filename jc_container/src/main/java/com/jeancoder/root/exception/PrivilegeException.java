package com.jeancoder.root.exception;

@SuppressWarnings("serial")
public class PrivilegeException extends RunningException {

	public PrivilegeException(String id, String app, String msg, Throwable cause) {
		super(id, app, msg, cause);
	}
	
	public PrivilegeException(String id, String app, String path, String msg, Throwable cause) {
		super(id, app, path, msg, cause);
	}
}
