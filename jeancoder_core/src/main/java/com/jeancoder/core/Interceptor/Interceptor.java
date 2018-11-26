package com.jeancoder.core.Interceptor;

public interface Interceptor {
	
	public abstract void setPreResource(String resource);
	public abstract String getPreResource();

	public abstract void setPostResource(String resource);
	public abstract String getPostResource();

	public void setHanRules(IntercepHanRule rule);
	public IntercepHanRule getHanRules();
	
	public boolean canExecute(String uri);
	
	public void bindHandler(Object handler);
	public Object getHandler();
}
