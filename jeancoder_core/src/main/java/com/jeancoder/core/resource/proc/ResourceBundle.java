package com.jeancoder.core.resource.proc;

import java.util.ArrayList;
import java.util.List;

/**
 * 以可执行粒度定义
 * @author jackielee
 *
 */
public class ResourceBundle {

	private List<Resource> resources = new ArrayList<Resource>();

	public List<Resource> getResources() {
		return resources;
	}

	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}
	
	public void addResources(Resource resources) {
		this.resources.add(resources);
	}
	
}
