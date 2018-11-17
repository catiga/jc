package com.jeancoder.core.power.exception;

import com.jeancoder.core.exception.JeancoderException;

public class DbPowerDoUpdateFailedException extends JeancoderException {
	private static final long serialVersionUID = 4209897150349941036L;

	public DbPowerDoUpdateFailedException(){
		
	}
	public DbPowerDoUpdateFailedException(Exception e){
		super(e);
	}
	public DbPowerDoUpdateFailedException(String message) {
		super(message);
	}
	@Override
	public String getMessage() {
		return super.getMessage();
	}
}
