package com.jeancoder.app.sdk.source;

import com.jeancoder.app.sdk.context.ApplicationContextPool;
import com.jeancoder.core.context.ApplicationContext;
import com.jeancoder.core.exception.SdkRuntimeException;
import com.jeancoder.core.http.JCResponse;

public class ResponseSource {
	public static JCResponse getResponse(){
		ApplicationContext ac = ApplicationContextPool.getApplicationContext();
		if (ac == null) {
			throw new SdkRuntimeException("ApplicationContext is null");
		}
		return ac.getResponse();
	}
}
