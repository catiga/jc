package com.jeancoder.app.sdk.source;

import com.jeancoder.core.cl.AppLoader;
import com.jeancoder.core.power.MemPower;
import com.jeancoder.root.container.ContainerContextEnv;
import com.jeancoder.root.container.JCAppContainer;

public class MemSource {

//	public static MemPower getMemPower(){
//		ApplicationContext ac = ApplicationContextPool.getApplicationContext();
//		if (ac == null) {
//			throw new SdkRuntimeException("ApplicationContext is null");
//		}
//		return ac.getMemPower();
//	}
	
	public static MemPower getMemPower(){
//		JCAppContainer container = JCAPPHolder.getContainer();
		JCAppContainer container = ContainerContextEnv.getCurrentContainer();
		if(container==null) {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			if(loader instanceof AppLoader) {
				AppLoader app_loader = (AppLoader)loader;
				
				container = app_loader.getContextEnv();
			}
		}
		return container.getCaps().getMemPower();
	}
}
