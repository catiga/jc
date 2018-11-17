package com.jeancoder.core.power.exception;

import com.jeancoder.core.exception.JeancoderException;
import com.jeancoder.core.power.PowerName;

public class PowerNameErrorException extends JeancoderException{
	private static final long serialVersionUID = 7592052738038303311L;
	
	private PowerName powerName;
	
	public PowerNameErrorException(PowerName powerName) {
		this.powerName = powerName;
	}

	@Override
	public String getMessage() {
		return "The PowerName \""+powerName.toString()+"\" incorrect.Please check source code."+super.getMessage();
	}
}
