package com.jeancoder.core.power;

public abstract class PowerHandler implements IPowerHandler {
	private String id;
	private boolean isDefault;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public boolean isDefault() {
		return isDefault;
	}
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
}
