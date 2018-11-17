package com.jeancoder.core.resource.proc;

import java.io.Serializable;

import com.jeancoder.core.resource.type.ResourceType;

@SuppressWarnings("serial")
public abstract class AbstractResource implements Resource, Serializable {

	private String rescontent;
	
	private ResourceType restype;
	
	private String resId;
	
	public AbstractResource(String rescontent, ResourceType restype) {
		this.rescontent = rescontent;
		this.restype = restype;
	}

	public String getRescontent() {
		return rescontent;
	}

	@Override
	public ResourceType getRestype() {
		return restype;
	}

	public String getResId() {
		return resId;
	}
	
	public void setResId(String resId) {
		this.resId = resId;
	}
	
}
