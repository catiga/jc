package com.jeancoder.root.container;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.jeancoder.root.container.core.BCID;

public class ContainerMaps {

	public final static Map<BCID, JCAppContainer> VM_CONTAINERS = new ConcurrentHashMap<BCID, JCAppContainer>();
	
	public Set<BCID> keySet() {
		return VM_CONTAINERS.keySet();
	}
	
	public JCAppContainer get(BCID app) {
		return VM_CONTAINERS.get(app);
	}
	
	public void put(BCID id, JCAppContainer container) {
		VM_CONTAINERS.put(id, container);
	}
	
	public void shutdown() {
		VM_CONTAINERS.forEach((k,v) -> {
			v.onStop();
		});
	}
}
