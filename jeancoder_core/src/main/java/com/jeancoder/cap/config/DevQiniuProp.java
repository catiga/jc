package com.jeancoder.cap.config;

import com.jeancoder.core.configure.PropItem;
import com.jeancoder.core.configure.PropType;

/**
 * 获取系统配置信息
 * @author huangjie
 *
 */
public class DevQiniuProp extends PropItem {

	public DevQiniuProp() {
		super(PropType.QINIU);
	}
	private String access;
	private String secret;
	private String defaultBucke;
	public String getAccess() {
		return access;
	}
	public void setAccess(String access) {
		this.access = access;
	}
	public String getSecret() {
		return secret;
	}
	public void setSecret(String secret) {
		this.secret = secret;
	}
	public String getDefaultBucke() {
		return defaultBucke;
	}
	public void setDefaultBucke(String defaultBucke) {
		this.defaultBucke = defaultBucke;
	}
	
}
