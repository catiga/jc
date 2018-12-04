package com.jc.task;

public abstract class ConcurrentTaskObject implements TaskObject {

	final Object __LOCK__ = new Object();
	
}
