package com.jeancoder.app.sdk;

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
}
