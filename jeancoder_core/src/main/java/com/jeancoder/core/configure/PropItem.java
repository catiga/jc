package com.jeancoder.core.configure;

public class PropItem {
	private String id;
	private Boolean isDefault = false;
	private PropType propType;
	
	public PropItem(PropType propType) {
		this.propType = propType;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Boolean getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}
	public PropType getPropType() {
		return propType;
	}
	public void setPropType(PropType propType) {
		this.propType = propType;
	}
	
}
