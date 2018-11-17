package com.jeancoder.app.thread;

import groovy.lang.Closure;

public class JCTaskBack {

	private Closure<?> callback;
	
	public JCTaskBack(Closure<?> callback) {
		this.callback = callback;
	}
	
	public void doAnything(TaskResult e) {
		System.out.println("prepare to do back");
		callback.call(e);
	}
}
