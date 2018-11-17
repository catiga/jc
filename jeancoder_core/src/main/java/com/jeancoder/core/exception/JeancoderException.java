package com.jeancoder.core.exception;

public class JeancoderException extends Exception {
	private static final long serialVersionUID = 7882438581771757081L;
	
	public JeancoderException(){
		
	}
	public JeancoderException(Exception e){
		super(e);
	}
	public JeancoderException(String message){
		super(message);
	}
	@Override
	public String getMessage() {
		return super.getMessage();
	}
}
