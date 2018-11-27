package com.jeancoder.core.configure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JeancoderConfigurer {
	private static Map<String,Props> propsMap = new HashMap<String,Props>();
	
	public static void register(Props props, String appCode) {
		if(!threadOK()) {
			return;
		}
		propsMap.put(appCode, props);
	}
	
	public static Props fetch(String appCode) {
		if(!threadOK()) {
			return null;
		}
		return propsMap.get(appCode);
	}
	
	public static List<PropItem> fetch(PropType propType,String appCode) {
		if(!threadOK()) {
			return null;
		}
		
		Props  props = propsMap.get(appCode);
		if (props == null) {
			return null;
		}
		return props.getProp(propType);
	}
	
	public static PropItem fetch(PropType propType,String id, String appCode) {
		if(!threadOK()) {
			return null;
		}
		Props  props = propsMap.get(appCode);
		if (props == null) {
			return null;
		}
		
		return props.getProp(propType,id);
	}
	
	public static PropItem fetchDefault(PropType propType, String appCode) {
		if(!threadOK()) {
			return null;
		}
		Props  props = propsMap.get(appCode);
		if (props == null) {
			return null;
		}
		
		return props.getDefault(propType);
	}
	
	public static boolean threadOK() {
//		return Thread.currentThread().getContextClassLoader() == JeancoderConfigurer.class.getClassLoader()
//				|| Thread.currentThread().getContextClassLoader().equals(Thread.currentThread().getClass().getClassLoader());
		return true;
	}
}
