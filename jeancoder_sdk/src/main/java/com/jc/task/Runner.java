package com.jc.task;

import groovy.lang.Closure;

public abstract class Runner {

	Closure<?> code;
	
	public Runner() {
		this.bind();
	}
	
	public abstract void bind();
	
	public void init(Closure<?> clos) {
		this.code = clos;
	}
	
	public void execute() {
		this.code.run();
	}
}
