package com.jeancoder.root.container.core;

import java.util.List;
import java.util.Map;

import com.jeancoder.root.container.JCAppContainer;
import com.jeancoder.root.container.JCVM;
import com.jeancoder.root.container.model.JCAPP;

public abstract class DefaultVm extends LifecycleZa implements JCVM {

	protected List<JCAPP> appList;
	
	@Override
	public Map<String, JCAppContainer> getContainers() {
		return JCVM.VM_CONTAINERS;
	}

	@Override
	public void setInitApps(List<JCAPP> appList) {
		this.appList = appList;
	}

}
