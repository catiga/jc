package com.jeancoder.core.power.exception;

import com.jeancoder.core.exception.JeancoderRuntimeException;

public class TransactionHasBeginException extends JeancoderRuntimeException {
	private static final long serialVersionUID = 3975752871522454845L;
	public TransactionHasBeginException(){
		
	}
	public TransactionHasBeginException(Exception e){
		super(e);
	}
	public TransactionHasBeginException(String message) {
		super(message);
	}
	@Override
	public String getMessage() {
		return "Current thread has begin a transaction."+super.getMessage();
	}
}
