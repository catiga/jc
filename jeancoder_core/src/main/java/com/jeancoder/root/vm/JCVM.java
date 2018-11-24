package com.jeancoder.root.vm;

import java.util.List;

import com.jeancoder.core.result.Result;
import com.jeancoder.root.container.ContainerMaps;
import com.jeancoder.root.env.JCAPP;
import com.jeancoder.root.env.Lifecycle;
import com.jeancoder.root.env.RunnerResult;
import com.jeancoder.root.io.http.JCHttpRequest;
import com.jeancoder.root.io.http.JCHttpResponse;

public interface JCVM extends Lifecycle {

	//public final static Map<BCID, JCAppContainer> VM_CONTAINERS = new ConcurrentHashMap<BCID, JCAppContainer>();
	public final static ContainerMaps VM_CONTAINERS = new ContainerMaps();
	
	public ContainerMaps getContainers();
	
	public void setInitApps(List<JCAPP> appList);
	
	public <T extends Result> RunnerResult<T> dispatch(JCHttpRequest req, JCHttpResponse res);
	
}
