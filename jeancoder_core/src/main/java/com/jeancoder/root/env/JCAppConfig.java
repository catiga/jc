package com.jeancoder.root.env;

import java.io.Serializable;

@SuppressWarnings("serial")
public class JCAppConfig implements Serializable {

	String description;
	
	String index;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}
	
}
