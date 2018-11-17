package com.jeancoder.app.sdk.exception;

import com.jeancoder.core.exception.AppException;

public class CommunicationPowerGenerateFailedException extends AppException {
	private static final long serialVersionUID = 5059574468347656846L;
	private String appcode;
	
	public CommunicationPowerGenerateFailedException(String appcode){
		this.appcode = appcode;
	}
	
	@Override
	public String getMessage() {
		return "Communication power '"+appcode+"' generate failed."+super.getMessage();
	}
}
