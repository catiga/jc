package com.jeancoder.app.sdk.remote;

import com.milepai.core.utils.web.JackSonBeanMapper;

public class RemoteCall {
	
	public static <T> T http_call(final Class<T> claz, String url, String param) {
		return http_call(claz, url, param, null);
	}
	
	public static String http_call(String url, String param) {
		return http_call(url, param, null);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T http_call(final Class<T> claz, String url, String param, RequestCert cert) {
		String ret = http_call(url, param, cert);
		T obj = (T)JackSonBeanMapper.fromJson(ret, claz);
		return obj;
	}
	
	public static String http_call(String url, String param, RequestCert cert) {
		String ret = HttpRequest.instance().getResponseString(url, param, HttpMethod.POST, cert);
		return ret;
	}
}
