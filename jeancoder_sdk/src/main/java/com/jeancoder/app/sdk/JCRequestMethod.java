package com.jeancoder.app.sdk;

import com.jeancoder.app.sdk.source.RequestSource;
import com.jeancoder.core.http.JCRequest;

public class JCRequestMethod implements JCMethod {

	public static JCRequest get() {
		return RequestSource.getRequest();
	}
	
	public static String param(String name) {
		return get().getParameter(name);
	}
	
}
