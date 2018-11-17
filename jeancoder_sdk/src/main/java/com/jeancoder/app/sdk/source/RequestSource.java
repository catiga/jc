package com.jeancoder.app.sdk.source;

import com.jeancoder.app.sdk.context.ApplicationContextPool;
import com.jeancoder.core.context.ApplicationContext;
import com.jeancoder.core.exception.SdkRuntimeException;
import com.jeancoder.core.http.JCRequest;

public class RequestSource {
	public static JCRequest getRequest(){
		ApplicationContext ac = ApplicationContextPool.getApplicationContext();
		if (ac == null) {
			throw new SdkRuntimeException("ApplicationContext is null");
		}
		return ac.getRequest();
	}
}
