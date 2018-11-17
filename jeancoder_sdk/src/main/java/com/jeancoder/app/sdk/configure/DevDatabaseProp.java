package com.jeancoder.app.sdk.configure;

import com.jeancoder.core.configure.PropItem;
import com.jeancoder.core.configure.PropType;

public class DevDatabaseProp extends PropItem{
	public DevDatabaseProp() {
		super(PropType.DATABASE);
	}
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
