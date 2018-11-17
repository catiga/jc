package com.jeancoder.jdbc.exception;

@SuppressWarnings("serial")
public class IDNotFoundException extends RuntimeException {

	public IDNotFoundException(String clz_name) {
		super(clz_name + " does not include ID field.");
	}
}
