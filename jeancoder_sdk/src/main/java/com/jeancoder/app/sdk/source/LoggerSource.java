package com.jeancoder.app.sdk.source;

import com.jeancoder.core.log.JCLogger;
import com.jeancoder.core.log.JCLoggerFactory;

public class LoggerSource {
	
	public static <T> JCLogger  getLogger(T t) {
		//return JCLoggerFactory.getLogger(t.getClass());
		return JCLoggerFactory.getLogger("APP-LEVEL-LOGGER");
	}
	
	public static  JCLogger  getLogger(String name) {
		//return JCLoggerFactory.getLogger(name);
		return JCLoggerFactory.getLogger("APP-LEVEL-LOGGER");
	}
	
	public static  JCLogger getLogger() {
		return JCLoggerFactory.getLogger("APP-LEVEL-LOGGER");
	}
}
