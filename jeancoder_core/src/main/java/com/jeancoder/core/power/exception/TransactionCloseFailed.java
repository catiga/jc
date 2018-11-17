package com.jeancoder.core.power.exception;

import com.jeancoder.core.exception.JeancoderRuntimeException;

public class TransactionCloseFailed extends JeancoderRuntimeException {
	private static final long serialVersionUID = -276050168458108205L;
	public TransactionCloseFailed(){
		
	}
	public TransactionCloseFailed(Exception e){
		super(e);
	}
	public TransactionCloseFailed(String message) {
		super(message);
	}
	@Override
	public String getMessage() {
		return "Try commit a not begin transaction."+super.getMessage();
	}
}
