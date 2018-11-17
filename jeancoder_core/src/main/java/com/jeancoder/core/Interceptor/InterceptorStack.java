package com.jeancoder.core.Interceptor;

/**
 * 拦截器栈
 * @author huangjie
 *
 */
public interface InterceptorStack {
	//添加拦截器
	public abstract void addInterceptor(Interceptor interceptor);
	//public abstract void addSysInterceptor(Interceptor interceptor);
}
