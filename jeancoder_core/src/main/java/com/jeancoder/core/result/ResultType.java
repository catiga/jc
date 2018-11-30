package com.jeancoder.core.result;

public enum ResultType {
	/**
	 * 数据类型
	 */
	DATA_RESOURCE,
	/**
	 * 控制器资源 Controller
	 */
	CONTROLLER_RESOURCE,
	/**
	 * 控制器资源
	 */
	REDIRECT_CONTROLLER_RESOURCE,
	/**
	 * 视图资源 html
	 */
	VIEW_RESOURCE,
	/**
	 * 静态资源 css  js
	 */
	STATIC_RESOURCE,
	
	GENERAL_IO,
	
	NULL,
}
