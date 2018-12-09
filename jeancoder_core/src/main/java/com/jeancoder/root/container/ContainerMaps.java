package com.jeancoder.root.container;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import com.jeancoder.root.container.core.BCID;

@SuppressWarnings("serial")
public class ContainerMaps implements Serializable {

	private final Map<BCID, JCAppContainer> VM_CONTAINERS = new ConcurrentHashMap<BCID, JCAppContainer>();
	
	public Set<BCID> keySet() {
		return VM_CONTAINERS.keySet();
	}
	
	public JCAppContainer get(BCID app) {
		return VM_CONTAINERS.get(app);
	}
	
	public Enumeration<JCAppContainer> getByCode(String code) {
		Vector<JCAppContainer> copies = new Vector<>();
		VM_CONTAINERS.forEach((k, v) -> {
			if(k.code().equals(code)) {
				copies.add(v);
			}
		});
		return copies.elements();
	}
	
	public void put(BCID id, JCAppContainer container) {
		VM_CONTAINERS.put(id, container);
	}
	
	public void remove(BCID id) {
		VM_CONTAINERS.remove(id);
	}
	
//	public void shutdown() {
////		VM_CONTAINERS.forEach((k,v) -> {
////			v.onStop();
////		});
//		for(BCID bcid : VM_CONTAINERS.keySet()) {
//			JCAppContainer container = VM_CONTAINERS.get(bcid);
//			container.onStop();
//			container.onDestroy();
//			VM_CONTAINERS.remove(bcid);
//		}
//	}
}
