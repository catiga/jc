package com.jeancoder.root.handler;

import java.util.EventObject;

import com.jeancoder.core.result.Result;

@SuppressWarnings("serial")
public class TouchIOEvent<T extends Result> extends EventObject {

	T result;
	
	public TouchIOEvent(T touch) {
		super(touch);
		this.result = touch;
	}
	
	public T getResult() {
		return result;
	}
	
}
