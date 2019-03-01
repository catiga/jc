package com.jeancoder.root.env;

import java.util.EventListener;
import java.util.LinkedHashSet;
import java.util.Set;

import com.jeancoder.core.result.Result;
import com.jeancoder.root.handler.RunnerResultListener;
import com.jeancoder.root.handler.TouchIOEvent;

public class RunnerResult<T extends Result> {

	Set<EventListener> listeners = new LinkedHashSet<EventListener>();

	JCAPP appins;

	String code;

	String id;

	String path;

	T result;
	
	Object data;

	public void setAppins(JCAPP appins) {
		this.appins = appins;
	}

	public JCAPP getAppins() {
		return appins;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Object getData() {
		return data;
	}

	public void addListener(EventListener listener) {
		listeners.add(listener);
	}

	@SuppressWarnings("unchecked")
	public void notifyListener() {
		for (EventListener listener : listeners) {
			((RunnerResultListener<T>) listener).handleEvent(new TouchIOEvent<T>(this.getResult()));
		}
	}
}
