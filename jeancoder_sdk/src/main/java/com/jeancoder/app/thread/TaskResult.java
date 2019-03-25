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

	public String getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public Object getData() {
		return data;
	}

	public CallbackException getEx() {
		return ex;
	}
	
}
