package com.jeancoder.core.log;

import org.slf4j.LoggerFactory;

public class JCLoggerFactory {
	
	@SuppressWarnings("rawtypes")
	public static <T> JCLogger  getLogger(Class t) {
		return new JCLogger(LoggerFactory.getLogger(t));
	}
	
	public static JCLogger getLogger(String name) {
		return new JCLogger(LoggerFactory.getLogger(name));
	}
}
