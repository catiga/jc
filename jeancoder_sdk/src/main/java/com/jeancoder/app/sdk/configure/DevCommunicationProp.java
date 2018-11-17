package com.jeancoder.app.sdk.configure;

import com.jeancoder.core.configure.PropItem;
import com.jeancoder.core.configure.PropType;

public class DevCommunicationProp extends PropItem{
	public DevCommunicationProp() {
		super(PropType.COMMUNICATION);
	}

	private String domain;
	
	private Integer deploy = 0;
	
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public Integer getDeploy() {
		return deploy;
	}
	public void setDeploy(Integer deploy) {
		this.deploy = deploy;
	}
	
}
