package com.jeancoder.app.sdk;

import com.jeancoder.app.sdk.remote.HttpMethod;
import com.jeancoder.app.sdk.remote.RemoteCall;
import com.jeancoder.app.sdk.remote.RequestCert;

public class JCRemoteMethod implements JCMethod {

	public static <T> T http_call(final Class<T> claz, String url, String params) {
		return RemoteCall.http_call(claz, url, params);
	}
	
	public static String http_call(String url, String params) {
		return RemoteCall.http_call(url, params);
	}
	
	public static <T> T http_call(final Class<T> claz, String url, String params, RequestCert cert) {
		return RemoteCall.http_call(claz, url, params, cert);
	}
	
	public static String http_call(String url, String params, RequestCert cert) {
		return RemoteCall.http_call(url, params, cert);
	}
	
	public static byte[] http_call_stream(String url, String params, RequestCert cert) {
		return RemoteCall.http_call_stream(url, params, cert);
	}
	
	public static byte[] http_call_stream(String url, String params) {
		return RemoteCall.http_call_stream(url, params, null);
	}
	
	public static byte[] http_call_stream(String url, String params, RequestCert cert, HttpMethod method) {
		return RemoteCall.http_call_stream(url, params, cert, method);
	}
	
	public static byte[] http_call_stream(String url, String params, HttpMethod method) {
		return RemoteCall.http_call_stream(url, params, null, method);
	}
}
