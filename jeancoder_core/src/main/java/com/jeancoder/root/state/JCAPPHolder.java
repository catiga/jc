package com.jeancoder.root.state;

import com.jeancoder.root.env.JCAPP;

public class JCAPPHolder {

	private static ThreadLocal<JCAPP> CURRENT_APPS = new ThreadLocal<>();
	
	public static void set(JCAPP ins) {
		CURRENT_APPS.set(ins);
	}
	
	public static JCAPP get() {
		return CURRENT_APPS.get();
	}
	
	public static void clear() {
		CURRENT_APPS.remove();
	}
}
