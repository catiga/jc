package com.jeancoder.jdbc.exception;

@SuppressWarnings("serial")
public class IDEmptyException extends RuntimeException {

	public IDEmptyException(String clz_name) {
		super(clz_name + " does not correct set ID value.");
	}
}
