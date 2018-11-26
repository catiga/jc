package com.jeancoder.app.sdk.Interceptor;

import com.jeancoder.core.Interceptor.IntercepHanRule;
import com.jeancoder.core.Interceptor.Interceptor;

public class JCInterceptor implements Interceptor {
	
	private String preResource = "";
	private String postResource = "";
	
	private Object handler;
	
	private IntercepHanRule rules;
	
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

	@Override
	public void setHanRules(IntercepHanRule rule) {
		this.rules = rule;
	}

	@Override
	public IntercepHanRule getHanRules() {
		return rules;
	}

	@Override
	public void bindHandler(Object handler) {
		this.handler = handler;
	}

	@Override
	public Object getHandler() {
		return handler;
	}

	public boolean canExecute(String uri) {
		if(rules==null) {
			//未设置则认为可以执行
			return true;
		}
		uri = disposeLast(uri, "/");
		//默认设置为不需要执行，也就是说当expass=true时候，才需要执行
		boolean expass = false;
		//first check mapping
		if(rules.getMapping()!=null) {
			//首先确认是否需要执行
			for(String s : rules.getMapping()) {
				if(uri.startsWith(s)) {
					expass = true;
					break;
				}
			}
		}
		//second check 是否允许放过
		if(expass) {
			if(rules.getExmapping()!=null) {
				for(String s : rules.getExmapping()) {
					if(uri.startsWith(s)) {
						expass = false;
						break;
					}
				}
			}
		}
		//默认为真
		return expass;
	}
	
	
	protected static String disposeLast(String uri, String charac) {
		uri = uri.trim();
		if(uri.equals(charac)) {
			return uri;
		}
		if(uri.endsWith(charac)) {
			uri = uri.substring(0, uri.length() - 1);
			if(uri.endsWith(charac)) {
				uri = disposeLast(uri, charac);
			}
		}
		return uri;
	}
	
	protected static String disposeFirst(String uri, String charac) {
		uri = uri.trim();
		if(uri.equals(charac)) {
			return uri;
		}
		if(uri.startsWith(charac)) {
			uri = uri.substring(1, uri.length());
			if(uri.startsWith(charac)) {
				uri = disposeFirst(uri, charac);
			}
		}
		return uri;
	}
	
}
