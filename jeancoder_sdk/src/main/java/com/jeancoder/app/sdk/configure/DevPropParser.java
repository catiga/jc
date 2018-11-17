package com.jeancoder.app.sdk.configure;

import java.util.Properties;
import java.util.regex.Pattern;

import com.jeancoder.core.configure.PropItem;
import com.jeancoder.core.configure.PropType;

public abstract class DevPropParser {
	private String patternString;
	
	public DevPropParser(String patternString) {
		this.patternString = patternString;
	}
	
	public String getPatternString() {
		return patternString;
	}
	public void setPatternString(String patternString) {
		this.patternString = patternString;
	}
	
	public boolean isMatcher(String pkey) {
		Pattern p = Pattern.compile(patternString);
		return p.matcher(pkey).matches();
	}
	
	public abstract String getId(String pkey);
	
	public abstract PropType getType();
	
	public abstract PropItem getInstance(String id);
	
	public abstract void setValue(Object instance,Properties p,String pkey)throws Exception;
}
