package com.jeancoder.app.sdk.source;

import com.jeancoder.core.power.MemPower;
import com.jeancoder.root.container.JCAppContainer;
import com.jeancoder.root.state.JCAPPHolder;

public class MemSource {

//	public static MemPower getMemPower(){
//		ApplicationContext ac = ApplicationContextPool.getApplicationContext();
//		if (ac == null) {
//			throw new SdkRuntimeException("ApplicationContext is null");
//		}
//		return ac.getMemPower();
//	}
	
	public static MemPower getMemPower(){
		JCAppContainer container = JCAPPHolder.getContainer();
		return container.getCaps().getMemPower();
	}
}
