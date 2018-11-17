package com.jeancoder.core.cl;

public class CLHandler {

	public volatile Class<?> loadedClass = null;

	public CLHandler(Class<?> loadedClass) {
		super();
		this.loadedClass = loadedClass;
	}
	
	public Class<?> getBindClass() {
		return loadedClass;
	}
}
