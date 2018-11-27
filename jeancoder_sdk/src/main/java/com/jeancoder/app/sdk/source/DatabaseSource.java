package com.jeancoder.app.sdk.source;

import com.jeancoder.core.cl.AppLoader;
import com.jeancoder.core.power.DatabasePower;
import com.jeancoder.root.container.ContainerContextEnv;
import com.jeancoder.root.container.JCAppContainer;

public class DatabaseSource {
//	public static DatabasePower getDatabasePower(){
//		ApplicationContext ac = ApplicationContextPool.getApplicationContext();
//		if (ac == null) {
//			throw new SdkRuntimeException("ApplicationContext is null");
//		}
//		return ac.getDatabase();
//	}
	
	public static DatabasePower getDatabasePower() {
		JCAppContainer container = ContainerContextEnv.getCurrentContainer();
		if(container==null) {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			if(loader instanceof AppLoader) {
				AppLoader app_loader = (AppLoader)loader;
				
				container = app_loader.getContextEnv();
			}
		}
		return container.getCaps().getDatabase();
	}
}
