package com.jeancoder.app.sdk.context;

import java.util.HashMap;
import java.util.Map;

import com.jeancoder.core.context.ApplicationContext;
import com.jeancoder.core.http.JCThreadLocal;

public class ApplicationContextPool {
	
	private static Map<String, ApplicationContext> applicationContextPool = new HashMap<String, ApplicationContext>();
	
	public static  void addApplicationContext(String appCode, ApplicationContext applicationContext) { 
		applicationContextPool.put(appCode, applicationContext);
	}
	
	public static  void removeApplicationContext(String appCode) { 
		applicationContextPool.remove(appCode);
	}
	
	public static ApplicationContext getApplicationContext() { 
		return applicationContextPool.get(JCThreadLocal.getCode());
	}
}
