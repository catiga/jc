package com.jeancoder.core.Interceptor;

public interface Interceptor {
	
	public abstract void setPreResource(String resource);
	public abstract String getPreResource();

	public abstract void setPostResource(String resource);
	public abstract String getPostResource();

}
