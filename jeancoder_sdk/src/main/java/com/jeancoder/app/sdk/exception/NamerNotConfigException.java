package com.jeancoder.app.sdk.exception;

import com.jeancoder.core.exception.AppException;

public class NamerNotConfigException extends AppException {
	private static final long serialVersionUID = -4666567571959498167L;
	
	private String appcode;
	
	public NamerNotConfigException(String appcode){
		this.appcode = appcode;
	}
	
	@Override
	public String getMessage() {
		return "Namer '"+appcode+"' not found.Please check your develop configure file."+super.getMessage();
	}
}
