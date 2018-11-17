package com.jeancoder.core.exception;

public class JeancoderRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 7882438581771757081L;
	
	public JeancoderRuntimeException(){
		
	}
	public JeancoderRuntimeException(Exception e){
		super(e);
	}
	public JeancoderRuntimeException(String message){
		super(message);
	}
	@Override
	public String getMessage() {
		return super.getMessage();
	}
}
