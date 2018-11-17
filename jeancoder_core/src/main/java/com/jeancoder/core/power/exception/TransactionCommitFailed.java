package com.jeancoder.core.power.exception;

import com.jeancoder.core.exception.JeancoderRuntimeException;

public class TransactionCommitFailed extends JeancoderRuntimeException {
	private static final long serialVersionUID = -4449193600172074091L;

	public TransactionCommitFailed(){
		
	}
	public TransactionCommitFailed(Exception e){
		super(e);
	}
	public TransactionCommitFailed(String message) {
		super(message);
	}
	@Override
	public String getMessage() {
		return "Try commit a not begin transaction."+super.getMessage();
	}
}
