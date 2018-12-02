package com.jeancoder.core.result;

import java.util.HashMap;
import java.util.Map;

public class Result {
	
	private ResultType resultType = ResultType.NULL;
	
	private Object data;
	
	private String result;
	
	private Map<String, Object> dataMap = new HashMap<>();
	
	/**
	 * 返回一个json 字符串
	 * @param result
	 */
	public Result setData(Object data) {
		this.data = data;
		this.resultType = ResultType.DATA_RESOURCE;
		return this;
	}
	
	/**
	 * 设置资源名称 默认为转发
	 * @param result
	 */
	public Result setResource(String resourceName) {
		this.result = resourceName;
		this.resultType = ResultType.CONTROLLER_RESOURCE;
		return this;
	}
	
	/**
	 * 设置资源名称 重定向
	 * @param result
	 */
	public Result setRedirectResource(String resourceName) {
		this.result = resourceName;
		this.resultType = ResultType.REDIRECT_CONTROLLER_RESOURCE;
		return this;
	}
	
	public Result setIO(String resourceName) {
		this.result = resourceName;
		this.resultType = ResultType.GENERAL_IO;
		return this;
	}
	
	
	/**
	 * 设置视图资源
	 * @param resources
	 */
	public Result setView(String resources) {
		this.result = resources;
		this.resultType = ResultType.VIEW_RESOURCE;
		return this;
	}
	
	/**
	 * 返回静态资源名称
	 * @param resources
	 * @return
	 */
	public Result setStaticName(String resources) {
		this.result = resources;
		this.resultType = ResultType.STATIC_RESOURCE;
		return this;
	}
	
	public ResultType getResultType() {
		return resultType;
	}

	public String getResult() {
		return result;
	}
	
	public Object getData() {
		return data;
	}
	
	public Result addObject(String attributeName, Object attributeValue) {
		dataMap.put(attributeName, attributeValue);
		return this;
	}
	
	public Map<String, Object> getDataMap() {
		return dataMap;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Result> T convert(Object general) {
		if(general==null) {
			Result result = new Result();
			result.setIO(null);
			return (T)result;
		} else {
			if(!(general instanceof Result)) {
				Result result = new Result();
				result.setData(general);
				result.resultType = ResultType.DATA_RESOURCE;
				return (T)result;
			}
			return (T)general;
		}
	}
}
