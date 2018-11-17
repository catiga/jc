package com.jeancoder.app.thread;

import groovy.lang.Closure;

public class JCTaskCall<O> {

	private Closure<O> callin;
	
	public JCTaskCall(Closure<O> callin) {
		this.callin = callin;
	}
	
	public O call() {
		return callin.call();
	}
}
