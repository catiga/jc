package com.jeancoder.core.power.exception;

import com.jeancoder.core.exception.JeancoderException;

public class DbPowerDoQueryFailedException extends JeancoderException {
	private static final long serialVersionUID = 4209897150349941036L;

	public DbPowerDoQueryFailedException(){}
	
	public DbPowerDoQueryFailedException(Exception e){
		super(e);
	}
	public DbPowerDoQueryFailedException(String message) {
		super(message);
	}
	@Override
	public String getMessage() {
		return super.getMessage();
	}
}
