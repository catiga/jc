package com.jeancoder.core.power.exception;

import com.jeancoder.core.exception.JeancoderRuntimeException;
import com.jeancoder.core.power.PowerName;

public class PowerUnsetException extends JeancoderRuntimeException {
	private static final long serialVersionUID = 1174527393716294645L;
	
	private PowerName powerName;
	
	public PowerUnsetException(){
		
	}
	public PowerUnsetException(Exception e){
		super(e);
	}
	public PowerUnsetException(String message) {
		super(message);
	}
	public PowerUnsetException(PowerName powerName) {
		this.powerName = powerName;
	}
	@Override
	public String getMessage() {
		return "The power "+powerName.toString() +" unset in your system."+super.getMessage();
	}
}
