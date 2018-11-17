package com.jeancoder.app.sdk.configure;

import com.jeancoder.core.configure.PropItem;
import com.jeancoder.core.configure.PropType;

public class DevSystemProp extends PropItem{
	public DevSystemProp() {
		super(PropType.APPLICATION);
	}

	private String code;
	private String appid;
	private String name;
	private String developercode;
	private String describe;
	private String index;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDevelopercode() {
		return developercode;
	}
	public void setDevelopercode(String developercode) {
		this.developercode = developercode;
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
