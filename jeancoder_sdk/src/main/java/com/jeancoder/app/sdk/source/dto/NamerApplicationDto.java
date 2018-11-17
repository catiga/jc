package com.jeancoder.app.sdk.source.dto;

public class NamerApplicationDto {
	//中文名称
	private String appName;
	//英文名称 唯一
	private String appCode;
	//描述信息
	private String describe;
	//默认首页可以为空
	private String index;
	
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getAppCode() {
		return appCode;
	}
	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
}
