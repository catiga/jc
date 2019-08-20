package com.jeancoder.app.sdk.remote;

import com.jeancoder.core.util.JackSonBeanMapper;

public class RemoteCall {
	
	public static <T> T http_call(final Class<T> claz, String url, String param) {
		return http_call(claz, url, param, null);
	}
	
	public static String http_call(String url, String param) {
		return http_call(url, param, null);
	}
	
	public static <T> T http_call(final Class<T> claz, String url, String param, RequestCert cert) {
		String ret = http_call(url, param, cert);
		T obj = (T)JackSonBeanMapper.fromJson(ret, claz);
		return obj;
	}
	
	// DEFAULT POST METHOD
	public static String http_call(String url, String param, RequestCert cert) {
		String ret = HttpRequest.instance().getResponseString(url, param, HttpMethod.POST, cert);
		return ret;
	}
	
	public static byte[] http_call_stream(String url, String params, RequestCert cert) {
		return HttpRequest.instance().getResponseStringAsStream(url, params, HttpMethod.POST, cert);
	}
	
	//SUPPORT METHOD SWITCH
	public static String http_call(String url, String param, RequestCert cert, HttpMethod method) {
		String ret = HttpRequest.instance().getResponseString(url, param, method, cert);
		return ret;
	}
	
	public static byte[] http_call_stream(String url, String params, RequestCert cert, HttpMethod method) {
		return HttpRequest.instance().getResponseStringAsStream(url, params, method, cert);
	}
}
