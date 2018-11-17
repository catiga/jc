package com.jeancoder.core.power.exception;

import com.jeancoder.core.exception.JeancoderException;

public class PowerDriveClassLoadFailedException extends JeancoderException{
	private static final long serialVersionUID = 1979671659005689938L;

	private String className;
	
	public PowerDriveClassLoadFailedException(String className){
		this.className = className;
	}
	
	@Override
	public String getMessage() {
		return "The drive class \""+className+"\" load failed."+super.getMessage();
	}
}
