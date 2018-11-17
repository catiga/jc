package com.jeancoder.app.sdk.source;

import com.jeancoder.app.sdk.context.ApplicationContextPool;
import com.jeancoder.core.context.ApplicationContext;
import com.jeancoder.core.exception.SdkRuntimeException;
import com.jeancoder.core.power.MemPower;

public class MemSource {

	public static MemPower getMemPower(){
		ApplicationContext ac = ApplicationContextPool.getApplicationContext();
		if (ac == null) {
			throw new SdkRuntimeException("ApplicationContext is null");
		}
		return ac.getMemPower();
	}
}
