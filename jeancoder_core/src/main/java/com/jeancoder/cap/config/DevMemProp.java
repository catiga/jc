package com.jeancoder.cap.config;

import com.jeancoder.core.configure.PropItem;
import com.jeancoder.core.configure.PropType;

public class DevMemProp extends PropItem {

	public DevMemProp() {
		super(PropType.MEM);
	}
	private String server;
	
	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

}
