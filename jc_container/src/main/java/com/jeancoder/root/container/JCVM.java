package com.jeancoder.root.container;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jeancoder.root.container.core.BCID;
import com.jeancoder.root.container.model.JCAPP;
import com.jeancoder.root.io.http.JCHttpRequest;
import com.jeancoder.root.io.http.JCHttpResponse;

public interface JCVM extends Lifecycle {

	static Map<BCID, JCAppContainer> VM_CONTAINERS = new ConcurrentHashMap<BCID, JCAppContainer>();
	
	public Map<BCID, JCAppContainer> getContainers();
	
	public void setInitApps(List<JCAPP> appList);
	
	public  <T> T dispatch(JCHttpRequest req, JCHttpResponse res);
	
}
