package com.jeancoder.app.sdk.context;

import java.util.HashMap;
import java.util.Map;

import com.jeancoder.core.common.Common;
import com.jeancoder.core.context.ApplicationContext;
import com.jeancoder.core.http.JCRequest;
import com.jeancoder.core.http.JCResponse;
import com.jeancoder.core.http.JCThreadLocal;
import com.jeancoder.core.power.DatabasePower;
import com.jeancoder.core.power.MemPower;
import com.jeancoder.core.power.QiniuPower;
import com.jeancoder.core.resource.proc.Application;
import com.jeancoder.core.result.Result;

public class JCApplicationContext implements ApplicationContext{
	
	private  Application application;
	
	private Map<String, Object> applicationContext = new HashMap<String, Object>();
	
	// 初始加载应用上下文
	public  JCApplicationContext() {
	}
	
	public void add(String key, Object obj) {
		applicationContext.put(key, obj);
	}

	@Override
	public JCRequest getRequest() {
		return JCThreadLocal.getRequest();
	}

	@Override
	public JCResponse getResponse() {
		
		return JCThreadLocal.getResponse();
	}

	@Override
	public DatabasePower getDatabase() {
		return (DatabasePower)applicationContext.get(Common.DATABASE);
	}

	@Override
	public Result getResult() {
		return JCThreadLocal.getResult();
	}

	@Override
	public void setResult(Result result) {
		JCThreadLocal.setResult(result);
	}

	@Override
	public ClassLoader getClassLoader() {
		return application.getClassLoader();
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	@Override
	public MemPower getMemPower() {
		// TODO Auto-generated method stub
		return (MemPower)applicationContext.get(Common.MEM_POWER);
	}

	@Override
	public QiniuPower getQiniuPower() {
		// TODO Auto-generated method stub
		return (QiniuPower)applicationContext.get(Common.QINIU_POWER);
	}
}
