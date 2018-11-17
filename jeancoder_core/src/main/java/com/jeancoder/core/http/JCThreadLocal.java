package com.jeancoder.core.http;

import java.util.Map;

import com.jeancoder.core.Interceptor.InterceptorChain;
import com.jeancoder.core.result.Result;

/**
 * 本类里面的线程变量允许开发者使用
 * @author huangjie
 *
 */
public class JCThreadLocal {
	
	private static  final  ThreadLocal<JCRequest> requestLocal = new InheritableThreadLocal<JCRequest>();
	private static  final  ThreadLocal<JCResponse> responseLocal = new InheritableThreadLocal<JCResponse>();
	private static  final  ThreadLocal<InterceptorChain> interceptorChainLocal = new InheritableThreadLocal<InterceptorChain>();
	private static  final  ThreadLocal<Result> resultLocal = new InheritableThreadLocal<Result>();
	private static  final  ThreadLocal<Map<String, Object>> nativeLocal = new InheritableThreadLocal<Map<String, Object>>();
	private static  final  ThreadLocal<String> appCodeLocal = new InheritableThreadLocal<String>();
	
	public static void setRequest(JCRequest request) {
		requestLocal.set(request);
	}
	public static JCRequest getRequest() {
		return requestLocal.get();
	}
	public static void setResponse(JCResponse response) {
		responseLocal.set(response);
	}
	public static JCResponse getResponse() {
		return responseLocal.get();
	}
	
	public static void setCode(String appCode) {
		appCodeLocal.set(appCode);
	}
	public static String getCode() {
		return appCodeLocal.get();
	}
	
	public static void setInterceptorChain(InterceptorChain chain) {
		interceptorChainLocal.set(chain);
	}
	public static InterceptorChain getnterceptorChain() {
		return interceptorChainLocal.get();
	}
	
	public static void setResult(Result result) {
		resultLocal.set(result);
	}
	public static Result getResult() {
		return resultLocal.get();
	}
	
	public static void setNativeParameter(Map<String, Object> parameter) {
		nativeLocal.set(parameter);
	}
	public static Map<String, Object> getNativeParameter() {
		return nativeLocal.get();
	}
	
	public static void remove() {
		requestLocal.remove();
		responseLocal.remove();
		appCodeLocal.remove();
		interceptorChainLocal.remove();
		resultLocal.remove();
		nativeLocal.remove();
	}
}
