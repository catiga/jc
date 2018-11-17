package com.jeancoder.root.container.core;

import com.jeancoder.app.sdk.Interceptor.JCInterceptorStack;
import com.jeancoder.app.sdk.configure.DevConfigureReader;
import com.jeancoder.app.sdk.configure.DevSysConfigProp;
import com.jeancoder.app.sdk.configure.DevSystemProp;
import com.jeancoder.app.sdk.context.ApplicationContextPool;
import com.jeancoder.app.sdk.context.JCApplicationContext;
import com.jeancoder.app.sdk.power.DevPowerLoader;
import com.jeancoder.core.common.Common;
import com.jeancoder.core.configure.JeancoderConfigurer;
import com.jeancoder.core.configure.PropType;
import com.jeancoder.core.http.JCThreadLocal;
import com.jeancoder.core.namer.DevLang;
import com.jeancoder.core.namer.FetchWay;
import com.jeancoder.core.namer.InstallWay;
import com.jeancoder.core.namer.NamerApplication;
import com.jeancoder.core.power.DatabasePower;
import com.jeancoder.core.power.DatabasePowerHandler;
import com.jeancoder.core.power.MemPower;
import com.jeancoder.core.power.MemPowerHandler;
import com.jeancoder.core.power.PowerHandlerFactory;
import com.jeancoder.core.power.PowerName;
import com.jeancoder.core.power.QiniuPower;
import com.jeancoder.core.power.QiniuPowerHandler;
import com.jeancoder.core.resource.proc.Application;
import com.jeancoder.core.resource.runtime.ApplicationHolder;
import com.jeancoder.root.container.JCAppContainer;
import com.jeancoder.root.container.loader.BootClassLoader;
import com.jeancoder.root.container.loader.TypeDefClassLoader;
import com.jeancoder.root.container.model.JCAPP;

public class BootContainer extends LifecycleZa implements JCAppContainer {

	private static BootClassLoader rootLoader = null;

	static final Object _LOCK_ = new Object();

	private TypeDefClassLoader containClassLoader = null;

	JCAPP appins;

	public BootContainer(JCAPP appins) {
		bindBaseEnv();
		this.appins = appins;
		this.onLoad();
		containClassLoader = new TypeDefClassLoader(rootLoader, appins);
		this.onStart();
	}

	@Override
	public void onLoad() {

	}

	@Override
	public void onStart() {
		super.onStart();
		String appPath = appins.getApp_base();
		DevConfigureReader.init(appPath, "appcode");
		// //初始化系统能力
		DevPowerLoader.init("appcode");
		DevSystemProp appcnf = (DevSystemProp) JeancoderConfigurer.fetchDefault(PropType.APPLICATION, "appcode");
		DevSysConfigProp sysConfig = (DevSysConfigProp) JeancoderConfigurer.fetchDefault(PropType.SYS, "appcode");

		NamerApplication app1 = new NamerApplication();
		app1.setAppCode(appcnf.getCode());
		app1.setDeveloperCode(appcnf.getDevelopercode());
		app1.setDevLang(DevLang.SDK_GROOVY);
		app1.setFetchWay(FetchWay.SDK);
		app1.setFetchAddress(appPath);
		app1.setInstallAddress(appPath);
		app1.setInstallWay(InstallWay.DISK);
		app1.setDescribe(appcnf.getDescribe());
		app1.setIndex(appcnf.getIndex());
		app1.setAppName(appcnf.getName());

		// InstallerFactory.generateInstaller(app1).install();
		Application app = new Application();
		app.setApp(app1);
		ApplicationHolder.getInstance().addApp(app);
		if (sysConfig != null) {
			app1.setInstallAddress(sysConfig.getLoadUri());
		}
		Application application = ApplicationHolder.getInstance().getAppByCode(app1.getAppCode());

		// //打印资源树
		// ApplicationHolder.getInstance().prinAll();
		// //初始应用相关的上下文
		JCApplicationContext jcApplicationContext = new JCApplicationContext();
		jcApplicationContext.setApplication(application);
		// db
		DatabasePowerHandler jcDatabasePower = (DatabasePowerHandler) PowerHandlerFactory
				.getPowerHandler(PowerName.DATABASE, "appcode");
		jcApplicationContext.add(Common.DATABASE, (DatabasePower) jcDatabasePower);
		// men
		MemPowerHandler memPowerHandler = (MemPowerHandler) PowerHandlerFactory.getPowerHandler(PowerName.MEM,
				"appcode");
		jcApplicationContext.add(Common.MEM_POWER, (MemPower) memPowerHandler);

		// 青牛
		QiniuPowerHandler qiniuPowerHandler = (QiniuPowerHandler) PowerHandlerFactory.getPowerHandler(PowerName.QINIU,
				"appcode");
		jcApplicationContext.add(Common.QINIU_POWER, (QiniuPower) qiniuPowerHandler);
		ApplicationContextPool.addApplicationContext(app1.getAppCode(), jcApplicationContext);

		// 注册拦截器
		// 执行初始化文件
		JCInterceptorStack.setNamerApplication(app1);
		JCThreadLocal.setCode(app1.getAppCode());
	}

	@Override
	public ClassLoader getManager() {
		return rootLoader;
	}

	private static void bindBaseEnv() {
		if (rootLoader == null) {
			synchronized (_LOCK_) {
				if (rootLoader == null) {
					rootLoader = new BootClassLoader(Thread.currentThread().getContextClassLoader());
				}
			}
		}
	}
}
