package com.jeancoder.core.power.exception;

import com.jeancoder.core.exception.JeancoderRuntimeException;

public class TransactionNotBeginException extends JeancoderRuntimeException {
	private static final long serialVersionUID = -7794300519227980684L;
	public TransactionNotBeginException(){
		
	}
	public TransactionNotBeginException(Exception e){
		super(e);
	}
	public TransactionNotBeginException(String message) {
		super(message);
	}
	@Override
	public String getMessage() {
		return "Current thread not begin a transaction."+super.getMessage();
	}
}
