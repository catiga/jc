package com.jeancoder.core.power;

/**
 * 关系型数据库配置
 * @author wow zhang_gh@cpis.cn
 * @date 2018年6月8日
 */
public class DatabasePowerConfig extends PowerConfig{
	private String driveClass;
	private String url;
	private String user;
	private String password;
	public String getDriveClass() {
		return driveClass;
	}
	public void setDriveClass(String driveClass) {
		this.driveClass = driveClass;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
