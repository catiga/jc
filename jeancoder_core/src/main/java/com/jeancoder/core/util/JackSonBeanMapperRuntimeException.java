package com.jeancoder.core.util;

@SuppressWarnings("serial")
public class JackSonBeanMapperRuntimeException extends RuntimeException{

	public JackSonBeanMapperRuntimeException(String msg) {
		super(msg);
	}
	
	public JackSonBeanMapperRuntimeException(String msg, Throwable ex) {
		super(msg, ex);
	}

	
}
