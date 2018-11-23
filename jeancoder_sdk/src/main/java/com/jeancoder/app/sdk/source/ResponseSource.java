package com.jeancoder.app.sdk.source;

import com.jeancoder.core.http.JCResponse;
import com.jeancoder.core.http.JCThreadLocal;

public class ResponseSource {
	public static JCResponse getResponse(){
//		ApplicationContext ac = ApplicationContextPool.getApplicationContext();
//		if (ac == null) {
//			throw new SdkRuntimeException("ApplicationContext is null");
//		}
//		return ac.getResponse();
		
		return JCThreadLocal.getResponse();
	}
}
