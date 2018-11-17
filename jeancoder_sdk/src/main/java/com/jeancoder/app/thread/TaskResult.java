package com.jeancoder.app.thread;

public class TaskResult {

	String code = "0";
	
	String msg = "success";
	
	Object data;
	
	CallbackException ex;

	public boolean getSuccess() {
		if(code.equals("0")) {
			return true;
		}
		return false;
	}
}
