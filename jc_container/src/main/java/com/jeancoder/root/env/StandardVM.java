package com.jeancoder.root.env;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	}

	@Override
	public synchronized void onStart() {
		logger.info("starting......");
		state = STATE_STARTING;
		//准备基础环境
		rootLoader = new BootClassLoader(Thread.currentThread().getContextClassLoader());
		if(this.sysLibs!=null)
			rootLoader.registerSysJars(this.sysLibs);
		
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
		logger.info("started");
		state = STATE_RUNNING;
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
