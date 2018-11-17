package com.jeancoder.core.power.exception;

import com.jeancoder.core.exception.JeancoderRuntimeException;

public class TransactionConnectFailed extends JeancoderRuntimeException {
	private static final long serialVersionUID = 7864725150605701393L;
	public TransactionConnectFailed(){
		
	}
	public TransactionConnectFailed(Exception e){
		super(e);
	}
	public TransactionConnectFailed(String message) {
		super(message);
	}
	@Override
	public String getMessage() {
		return "Try commit a not begin transaction."+super.getMessage();
	}
}
