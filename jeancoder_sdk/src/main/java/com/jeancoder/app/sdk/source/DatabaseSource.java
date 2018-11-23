package com.jeancoder.app.sdk.source;

import com.jeancoder.core.power.DatabasePower;
import com.jeancoder.root.container.JCAppContainer;
import com.jeancoder.root.state.JCAPPHolder;

public class DatabaseSource {
//	public static DatabasePower getDatabasePower(){
//		ApplicationContext ac = ApplicationContextPool.getApplicationContext();
//		if (ac == null) {
//			throw new SdkRuntimeException("ApplicationContext is null");
//		}
//		return ac.getDatabase();
//	}
	
	public static DatabasePower getDatabasePower() {
		JCAppContainer container = JCAPPHolder.getContainer();
		return container.getCaps().getDatabase();
	}
}
