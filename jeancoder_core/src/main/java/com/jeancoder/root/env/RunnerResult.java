package com.jeancoder.root.env;

import com.jeancoder.core.result.Result;

public class RunnerResult<T extends Result> {
	
	JCAPP appins;

	String code;
	
	String id;
	
	String path;
	
	T result;

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

}
