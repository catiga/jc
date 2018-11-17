package com.jeancoder.root.container;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jeancoder.root.container.model.JCAPP;

public interface JCVM extends Lifecycle {

	static Map<String, JCAppContainer> VM_CONTAINERS = new ConcurrentHashMap<String, JCAppContainer>();
	
	public Map<String, JCAppContainer> getContainers();
	
	public void setInitApps(List<JCAPP> appList);
}
