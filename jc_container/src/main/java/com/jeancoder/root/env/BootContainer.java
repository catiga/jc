package com.jeancoder.root.env;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeancoder.app.sdk.Interceptor.JCInterceptorChain;
import com.jeancoder.app.sdk.Interceptor.JCInterceptorStack;
import com.jeancoder.app.sdk.configure.DevConfigureReader;
import com.jeancoder.app.sdk.configure.DevSysConfigProp;
import com.jeancoder.app.sdk.configure.DevSystemProp;
import com.jeancoder.app.sdk.context.ApplicationContextPool;
import com.jeancoder.app.sdk.context.JCApplicationContext;
import com.jeancoder.app.sdk.power.DevPowerLoader;
import com.jeancoder.app.sdk.source.jeancoder.SysSource;
import com.jeancoder.core.Interceptor.Interceptor;
import com.jeancoder.core.common.Common;
import com.jeancoder.core.configure.JeancoderConfigurer;
import com.jeancoder.core.configure.PropType;
import com.jeancoder.core.http.JCRequest;
import com.jeancoder.core.http.JCResponse;
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
import com.jeancoder.core.result.Result;
import com.jeancoder.root.container.JCAppContainer;
import com.jeancoder.root.container.core.BCID;
import com.jeancoder.root.container.core.DefaultContainer;
import com.jeancoder.root.container.loader.BootClassLoader;
import com.jeancoder.root.container.loader.TypeDefClassLoader;
import com.jeancoder.root.exception.Code404Exception;
import com.jeancoder.root.exception.Code500Exception;
import com.jeancoder.root.exception.CompileException;
import com.jeancoder.root.exception.PrivilegeException;
import com.jeancoder.root.io.http.JCHttpRequest;
import com.jeancoder.root.io.http.JCHttpResponse;

import groovy.lang.Binding;
import groovy.lang.Script;

public class BootContainer extends DefaultContainer implements JCAppContainer {

	private static Logger logger = LoggerFactory.getLogger(BootContainer.class);

	private static BootClassLoader rootLoader = null;

	static final Object _LOCK_ = new Object();

	volatile String state = STATE_READY;

	@Override
	public BCID id() {
		return BCID.generateKey(appins.getId(), appins.getCode());
	}

	protected String state() {
		return state;
	}

	protected BootContainer(JCAPP appins) {
		bindBaseEnv();
		this.appins = appins;
		this.onInit();
		this.onStart();
	}
	
	protected Object singleRun(JCHttpRequest req, JCHttpResponse res, String classname) {
		try {
			Class<?> executor = containClassLoader.getAppClassLoader().findClass(classname);
			Binding context = new Binding();
			Script script = (Script) executor.newInstance();
			script.setBinding(context);
			Object result = script.run();
			return result;
		} catch (IllegalAccessException e) {
			throw new PrivilegeException(id().id(), id().code(), this.transferPathToClz(req), "SCRIPT_PRIVILEGE_ERROR", e);
		} catch (InstantiationException nfex) {
			throw new CompileException(id().id(), id().code(), this.transferPathToClz(req), "PROGRAM_COMPILE_ERROR", nfex);
		} catch (ClassNotFoundException nfex) {
			//throw new Code404Exception(id().id(), id().code(), this.transferPathToClz(req), "CLASS_NOT_FOUND", nfex);
			return true;
		} catch (Exception ex) {
			logger.error("", ex);
			throw new Code500Exception(id().id(), id().code(), this.transferPathToClz(req), "RUNNING_ERROR", ex);
		}
	}

	@Override
	public <T extends Result> RunnerResult<T> execute(JCHttpRequest req, JCHttpResponse res) {
		SysSource.setClassLoader(containClassLoader.getAppClassLoader());
		JCThreadLocal.setRequest(new JCRequest(req));
		JCThreadLocal.setResponse(new JCResponse(res));
		
		String resourceId = this.transferPathToClz(req);
		JCInterceptorChain chain = JCInterceptorStack.getJCInterceptorChain(this.appins.getCode(), resourceId);
		//直接循环执行
		for(Interceptor i : chain.getInterceptorChain()) {
			Object sing_run_result = singleRun(req, res, i.getPreResource());
			System.out.println(sing_run_result);
		}
		
		// 进入拦截器
//		chain.doInterceptor(chain, application, resourceId.toString(), req, res);
//		Result result =  Runner.getResult(application, req, res);
//		if (result == null) {
//			res.setContentType("text/html;charset=UTF-8");
//			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
//			return ;
//		}
		
		try {
			Class<?> executor = this.transferPathToIns(req);
			Binding context = new Binding();
			// Constructor<?> constructor =
			// executor.getConstructor(Binding.class);
			// entry for run must be Script type;
			Script script = (Script) executor.newInstance();
			script.setBinding(context);
			Object result = script.run();
			RunnerResult<T> ret_result = new RunnerResult<>();
			ret_result.setResult(Result.convert(result));
			ret_result.setId(id().id());
			ret_result.setCode(id().code());
			ret_result.setPath(this.transferPathToClz(req));
			ret_result.setAppins(this.appins);
			return ret_result;
		} catch (IllegalAccessException e) {
			throw new PrivilegeException(id().id(), id().code(), this.transferPathToClz(req), "SCRIPT_PRIVILEGE_ERROR", e);
		} catch (InstantiationException nfex) {
			throw new CompileException(id().id(), id().code(), this.transferPathToClz(req), "PROGRAM_COMPILE_ERROR", nfex);
		} catch (ClassNotFoundException nfex) {
			throw new Code404Exception(id().id(), id().code(), this.transferPathToClz(req), "CLASS_NOT_FOUND", nfex);
		} catch (Exception ex) {
			logger.error("", ex);
			throw new Code500Exception(id().id(), id().code(), this.transferPathToClz(req), "RUNNING_ERROR", ex);
		}
	}

	@Override
	public void onStart() {
		synchronized (_LOCK_) {
			if (!state.equals(STATE_INITED)) {
				throwCause(null);
			}
			state = STATE_STARTING;
		}
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
		//JCInterceptorStack.setNamerApplication(app1);
		JCThreadLocal.setCode(app1.getAppCode());

		state = STATE_RUNNING;
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

	@Override
	public void onInit() {
		synchronized (_LOCK_) {
			if (!state.equals(STATE_READY)) {
				throwCause(null);
			}
			containClassLoader = new TypeDefClassLoader(rootLoader, appins);
			state = STATE_INITED;
		}
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub

	}

	protected void throwCause(String msg) {
		if (msg == null) {
			msg = "CONTAINER STATUS INVALID";
		}
		throw new RuntimeException(msg);
	}
}
