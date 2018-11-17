package com.jeancoder.core.namer;

import com.jeancoder.core.exception.AppInstallerException;
import com.jeancoder.core.log.JCLogger;
import com.jeancoder.core.log.JCLoggerFactory;

public class InstallerFactory {
	
	private static final JCLogger LOGGER = JCLoggerFactory.getLogger(InstallerFactory.class);
	
	public static NamerAppInstaller generateInstaller(NamerApplication application) {
		try {
			NamerAppInstaller namerAppInstaller = new NamerAppInstaller();
			namerAppInstaller.setApplication(application);
			// 获取器
			namerAppInstaller.setFetch(application.getFetchWay().getInstance());
			// 载入器
			namerAppInstaller.setLoad(application.getInstallWay().getInstance());
			// 解释器
			namerAppInstaller.setParse(application.getDevLang().getInstance());
			LOGGER.debug("Application " + application.getAppCode() + " obtain NamerAppInstaller success");
			return namerAppInstaller;
		} catch ( Exception e) {
			e.printStackTrace();
			throw new AppInstallerException(e.getMessage(), e);
		} 
		//载入器
		
	}
}
