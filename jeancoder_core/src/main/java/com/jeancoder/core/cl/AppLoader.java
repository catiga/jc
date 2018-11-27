package com.jeancoder.core.cl;

public interface AppLoader extends KoLoader {

	public CLHandler[] getAppClasses();
	
	public Class<?> findClass(String name) throws ClassNotFoundException;
}
