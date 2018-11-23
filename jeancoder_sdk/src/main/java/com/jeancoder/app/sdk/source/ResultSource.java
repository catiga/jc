package com.jeancoder.app.sdk.source;

import com.jeancoder.core.http.JCThreadLocal;
import com.jeancoder.core.result.Result;

public class ResultSource {
	public static Result getResult(){
//		ApplicationContext ac = ApplicationContextPool.getApplicationContext();
//		if (ac == null) {
//			throw new SdkRuntimeException("ApplicationContext is null");
//		}
//		return ac.getResult();
		return JCThreadLocal.getResult();
	}
	
	public static void setResult(Result result){
//		ApplicationContext ac = ApplicationContextPool.getApplicationContext();
//		if (ac == null) {
//			throw new SdkRuntimeException("ApplicationContext is null");
//		}
//		ac.setResult(result);
		JCThreadLocal.setResult(result);
	}
}
