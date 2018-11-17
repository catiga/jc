package com.jeancoder.root.container.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeancoder.root.container.JCVM;
import com.jeancoder.root.container.model.JCAPP;

public class StandardVM extends DefaultVm implements JCVM {

	private static Logger logger = LoggerFactory.getLogger(StandardVM.class);
	
	@Override
	public synchronized void onInit() {
		logger.info("init boot vm");
		super.onInit();
	}

	@Override
	public synchronized void onStart() {
		this.onInit();
		logger.info("开始启动app 容器");
		if(this.appList!=null) {
			for(JCAPP jca : this.appList) {
				BootContainer bc = new BootContainer(jca);
				VM_CONTAINERS.put(jca.getId() + jca.getCode(), bc);
			}
		}
	}

	@Override
	public synchronized void onStop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public synchronized void onDestroy() {
		// TODO Auto-generated method stub
		
	}
	
}
