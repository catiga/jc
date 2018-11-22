package com.jeancoder.root.exception;

import com.jeancoder.root.env.JCAPP;

@SuppressWarnings("serial")
public class Code500Exception extends RunningException {

	public Code500Exception(JCAPP appins, String res, String path, String msg, Throwable cause) {
		super(appins.getId(), appins.getCode(), res, path, msg, cause);
		this.appins = appins;
	}

	public Code500Exception(String id, String app, String res, String path, String msg, Throwable cause) {
		super(id, app, res, path, msg, cause);
	}
}
