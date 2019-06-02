package com.jeancoder.root.container.core;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BCID implements Serializable {

	String id;
	
	String code;

	public String id() {
		return id;
	}

	public String code() {
		return code;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((id == null) ? 0 : id.hashCode());
//		return result;
//	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BCID other = (BCID) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	public static BCID generateKey(String id, String code) {
		BCID bcid = new BCID();
		bcid.id = id;
		bcid.code = code;
		return bcid;
	}
}
