package com.jeancoder.root.env;

import java.io.File;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeancoder.core.util.FileUtil;
import com.jeancoder.root.container.JCAppContainer;
import com.jeancoder.root.container.core.BCID;
import com.jeancoder.root.container.loader.BootClassLoader;
import com.jeancoder.root.vm.DefaultVm;
import com.jeancoder.root.vm.JCVM;

public class StandardVM extends DefaultVm implements JCVM {

	private static Logger logger = LoggerFactory.getLogger(StandardVM.class);
	
	private final static StandardVM INSTANCE = new StandardVM();
	
	private BootClassLoader rootLoader = null;
	
	public static StandardVM getVM() {
		synchronized (StandardVM.class) {
			if(state.equals(STATE_READY)) {
				INSTANCE.onInit();
			}
		}
		return INSTANCE;
	}
	
	private StandardVM() {
	}
	
	@Override
	public synchronized void onInit() {
		logger.info("init boot vm");
		state = STATE_INITED;
		//准备基础环境
		rootLoader = new BootClassLoader(Thread.currentThread().getContextClassLoader());
	}

	@Override
	public synchronized void onStart() {
		logger.info("JC VM STARTING......");
		state = STATE_STARTING;
		if(this.sysLibs!=null)
			rootLoader.registerSysJars(this.sysLibs);
		Thread.currentThread().setContextClassLoader(rootLoader);	//reset context
		if(this.appList!=null) {
			for(JCAPP jca : this.appList) {
				BootContainer bc = new BootContainer(jca);
				bc.bindBaseEnv(rootLoader);
				bc.onInit();
				bc.onStart();
				if(bc.state().equals(STATE_RUNNING)) {
					VM_CONTAINERS.put(bc.id(), bc);
				} else {
					logger.error(bc.id() + " starting error.");
				}
			}
		}
		logger.info("JC VM STARTED");
		state = STATE_RUNNING;
	}

	@Override
	public void updateApp(JCAPP jcapp) {
		BootContainer bc = new BootContainer(jcapp);
		bc.bindBaseEnv(rootLoader);
		bc.onInit();
		bc.onStart();
		
		Enumeration<JCAppContainer> appContainer  = VM_CONTAINERS.getByCode(jcapp.getCode());
		JCAppContainer container = appContainer.nextElement();
		if (container == null) {
			VM_CONTAINERS.put(bc.id(), bc);
			return;
		}
		if (bc.id().equals(container.id())) {
			VM_CONTAINERS.put(bc.id(), bc);
		} else {
			VM_CONTAINERS.remove(container.id());
			VM_CONTAINERS.put(bc.id(), bc);
		}
		FileUtil.deletefile(new File(container.getApp().getApp_base()));
	}
	
	
	
	@Override
	public synchronized void onStop() {
		for(BCID bcid : VM_CONTAINERS.keySet()) {
			JCAppContainer container = VM_CONTAINERS.get(bcid);
			if(container.state().equals(Lifecycle.STATE_RUNNING))
				container.onStop();
			container.changeState(Lifecycle.STATE_STOPED);
		}
	}

	@Override
	public synchronized void onDestroy() {
		for(BCID bcid : VM_CONTAINERS.keySet()) {
			JCAppContainer container = VM_CONTAINERS.get(bcid);
			if(container.state().equals(Lifecycle.STATE_STOPED))
				container.onDestroy();
			VM_CONTAINERS.remove(bcid);
		}
	}
	
}
