package com.jeancoder.app.sdk.Interceptor;

import com.jeancoder.core.Interceptor.Interceptor;

public class JCInterceptor implements Interceptor {
	
	private String preResource = "";
	private String postResource = "";
	
	@Override
	public String getPreResource() {
		return preResource;
	}
	
	@Override
	public void setPreResource(String preResource) {
		this.preResource = preResource;
	}
	
	@Override
	public String getPostResource() {
		return postResource;
	}
	
	@Override
	public void setPostResource(String postResource) {
		this.postResource = postResource;
	}

}
