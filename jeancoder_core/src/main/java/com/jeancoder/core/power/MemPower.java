package com.jeancoder.core.power;

public interface MemPower {

	public boolean set(String k, Object e);
	
	public boolean setUntil(String k, Object v, Long exp);
	
	public Object get(String k);
	
	public Object delete(String k);
	
	
	public boolean setAsString(String k, Object e);
	
	public boolean setUntilAsString(String k, Object v, Long exp);
	
	public String getAsString(String k);
	
	public String deleteAsString(String k);
	
}
