package com.jeancoder.root.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.jeancoder.root.container.ContainerMaps;
import com.jeancoder.root.container.JCAppContainer;
import com.jeancoder.root.container.core.BCID;
import com.jeancoder.root.env.JCAPP;

@SuppressWarnings("serial")
public class ContainerBean implements Serializable {

	BCID id;
	
	JCAPP app;
	
	public ContainerBean() {}
	
	public ContainerBean(JCAppContainer container) {
		this.id = container.id();
		this.app = container.getApp();
	}

	public BCID getId() {
		return id;
	}

	public void setId(BCID id) {
		this.id = id;
	}

	public JCAPP getApp() {
		return app;
	}

	public void setApp(JCAPP app) {
		this.app = app;
	}

	public static Map<BCID, ContainerBean> cons(ContainerMaps maps) {
		Map<BCID, ContainerBean> result = new HashMap<>();
		maps.keySet().forEach(bcid-> {
			JCAppContainer container = maps.get(bcid);
			result.put(bcid, new ContainerBean(container));
		});
		return result;
	}
	
}
