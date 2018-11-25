package com.jeancoder.root.container;

import java.util.Enumeration;

import com.jeancoder.core.Interceptor.Interceptor;
import com.jeancoder.core.cl.DefLoader;
import com.jeancoder.core.cl.JCLoader;
import com.jeancoder.core.result.Result;
import com.jeancoder.root.container.core.BCID;
import com.jeancoder.root.env.JCAPP;
import com.jeancoder.root.env.Lifecycle;
import com.jeancoder.root.env.RunnerResult;
import com.jeancoder.root.io.http.JCHttpRequest;
import com.jeancoder.root.io.http.JCHttpResponse;

public interface JCAppContainer extends Lifecycle {
	
	JCLoader getManagerClassLoader();
	
	DefLoader getSignedClassLoader();
	
	public BCID id();
	
	<T extends Result> RunnerResult<T> execute(JCHttpRequest req, JCHttpResponse res);
	
	//<T extends Result> RunnerResult<T> run(JCHttpRequest req, JCHttpResponse res);
	
	<T extends Result> RunnerResult<T> execute(String path);
	
	JCAPP getApp();
	
	PowerCaps getCaps();
	
	public void addInterceptor(Interceptor interceptor);
	
	public Enumeration<Interceptor> interceptors();
}
