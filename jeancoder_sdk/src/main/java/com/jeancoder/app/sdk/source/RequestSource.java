package com.jeancoder.app.sdk.source;

import com.jeancoder.core.http.JCRequest;
import com.jeancoder.core.http.JCThreadLocal;

public class RequestSource {
	public static JCRequest getRequest(){
//		ApplicationContext ac = ApplicationContextPool.getApplicationContext();
//		if (ac == null) {
//			throw new SdkRuntimeException("ApplicationContext is null");
//		}
//		return ac.getRequest();
		return JCThreadLocal.getRequest();
	}
}
