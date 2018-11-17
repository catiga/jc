package com.jeancoder.app.sdk.source;

import com.jeancoder.app.sdk.context.ApplicationContextPool;
import com.jeancoder.core.context.ApplicationContext;
import com.jeancoder.core.exception.SdkRuntimeException;
import com.jeancoder.core.power.QiniuPower;

public class QiniuSource {
	/**
	 * 获取调用其他app能力
	 * @param appcode 另一个app的code 需要在application.properties中配置（例：namer.system.domain=http://localhost:8000,system为id）
	 * @return
	 */
	public static QiniuPower getQiniuPower(){
		
		ApplicationContext ac = ApplicationContextPool.getApplicationContext();
		if (ac == null) {
			throw new SdkRuntimeException("ApplicationContext is null");
		}
		return ac.getQiniuPower();
	}
}
