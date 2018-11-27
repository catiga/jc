package com.jeancoder.app.sdk.source;

import com.jeancoder.core.cl.AppLoader;
import com.jeancoder.core.power.QiniuPower;
import com.jeancoder.root.container.ContainerContextEnv;
import com.jeancoder.root.container.JCAppContainer;

public class QiniuSource {
	/**
	 * 获取调用其他app能力
	 * @param appcode 另一个app的code 需要在application.properties中配置（例：namer.system.domain=http://localhost:8000,system为id）
	 * @return
	 */
//	public static QiniuPower getQiniuPower(){
//		
//		ApplicationContext ac = ApplicationContextPool.getApplicationContext();
//		if (ac == null) {
//			throw new SdkRuntimeException("ApplicationContext is null");
//		}
//		return ac.getQiniuPower();
//	}
	
	public static QiniuPower getQiniuPower(){
//		JCAppContainer container = JCAPPHolder.getContainer();
		JCAppContainer container = ContainerContextEnv.getCurrentContainer();
		if(container==null) {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			if(loader instanceof AppLoader) {
				AppLoader app_loader = (AppLoader)loader;
				
				container = app_loader.getContextEnv();
			}
		}
		return container.getCaps().getQiniu();
	}
}
