package com.jeancoder.core.cl;

public interface AppLoader extends JCLoader {

	public CLHandler[] getAppClasses();
	
	public Class<?> findClass(String name) throws ClassNotFoundException;
}
