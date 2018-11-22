package com.jeancoder.root.exception;

import com.jeancoder.root.env.JCAPP;

@SuppressWarnings("serial")
public class RunningException extends RuntimeException {

	String id;
	
	String app;
	
	String path;
	
	String res;
	
	protected JCAPP appins;

	public RunningException(String id, String app, String res, String path, String msg, Throwable cause) {
		super(msg, cause);
		this.id = id;
		this.app = app;
		this.res = res;
		this.path = path;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getRes() {
		return res;
	}

	public void setRes(String res) {
		this.res = res;
	}
	
}
