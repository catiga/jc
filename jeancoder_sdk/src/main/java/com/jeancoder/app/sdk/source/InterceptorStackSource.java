package com.jeancoder.app.sdk.source;

import com.jeancoder.app.sdk.Interceptor.JCInterceptorStack;
import com.jeancoder.core.Interceptor.InterceptorStack;

/**
 * 得到当前App 请求的拦截器栈
 * @author huangjie
 *
 */
public class InterceptorStackSource {
	public static InterceptorStack getInterceptorStack(){
		return (InterceptorStack)JCInterceptorStack.getInterceptorStack();
	}
}
