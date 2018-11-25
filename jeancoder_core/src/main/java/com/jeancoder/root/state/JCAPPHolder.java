package com.jeancoder.root.state;

import com.jeancoder.root.container.JCAppContainer;

public class JCAPPHolder {

//	private static ThreadLocal<JCAPP> CURRENT_APPS = new ThreadLocal<>();
	
	private static ThreadLocal<JCAppContainer> CURRENT_CONTAINERS = new ThreadLocal<>();
	
//	public static void set(JCAPP ins) {
//		CURRENT_APPS.set(ins);
//	}
//	
//	public static JCAPP get() {
//		return CURRENT_APPS.get();
//	}
//	
//	public static void clear() {
//		CURRENT_APPS.remove();
//	}
	
	public static void setContainer(JCAppContainer c) {
		CURRENT_CONTAINERS.set(c);
	}
	
	public static JCAppContainer getContainer() {
		return CURRENT_CONTAINERS.get();
	}
	
	public static void clearContainer() {
		CURRENT_CONTAINERS.remove();
	}
}
