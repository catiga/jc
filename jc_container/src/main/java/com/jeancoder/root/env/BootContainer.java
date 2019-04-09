package com.jeancoder.root.env;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeancoder.cap.config.DevConfigureReader;
import com.jeancoder.cap.power.DevPowerLoader;
import com.jeancoder.core.cl.DefLoader;
import com.jeancoder.core.cl.JCLoader;
import com.jeancoder.core.common.Common;
import com.jeancoder.core.http.JCThreadLocal;
import com.jeancoder.core.power.DatabasePowerHandler;
import com.jeancoder.core.power.MemPowerHandler;
import com.jeancoder.core.power.PowerHandlerFactory;
import com.jeancoder.core.power.PowerName;
import com.jeancoder.core.power.QiniuPowerHandler;
import com.jeancoder.core.result.Result;
import com.jeancoder.root.container.ContainerContextEnv;
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
import com.jeancoder.root.io.line.HeaderNames;

import groovy.lang.Binding;
import groovy.lang.Script;
import io.netty.handler.codec.http.HttpResponseStatus;

@SuppressWarnings("serial")
public class BootContainer extends DefaultContainer implements JCAppContainer {

	private static Logger logger = LoggerFactory.getLogger(BootContainer.class.getName());

	private BootClassLoader rootLoader = null;

	static final Object _LOCK_ = new Object();

	volatile String state = STATE_READY;
	
	@Override
	public BCID id() {
		return BCID.generateKey(appins.getId(), appins.getCode());
	}

	public String state() {
		return state;
	}

	protected BootContainer(JCAPP appins) {
		//bindBaseEnv();
		this.appins = appins;
//		this.onInit();
//		this.onStart();
	}
	
	@Override
	public DefLoader getSignedClassLoader() {
		return this.containClassLoader;
	}

	@Override
	public void changeState(String lifeCycleState) {
		this.state = lifeCycleState;
	}

	@Override
	protected <T extends Result> RunnerResult<T> run(JCHttpRequest req, JCHttpResponse res) {
		Class<?> executor = null;
		try {
			executor = this.transferPathToIns(req);
			Binding context = new Binding();
			// Constructor<?> constructor =
			// executor.getConstructor(Binding.class);
			// entry for run must be Script type;
			Script script = (Script) executor.newInstance();
			script.setBinding(context);
			Object result = script.run();
			if(result==null) {
				result = JCThreadLocal.getResult();
			}
			if(res.getStatus()==HttpResponseStatus.FOUND.code()) {
				String redirect_uri = res.getHeader(HeaderNames.LOCATION);
				if(redirect_uri!=null) {
					String default_schema = req.getScheme();
					if(default_schema!=null) {
						//替换schema
						if(redirect_uri.toLowerCase().startsWith("https")) {
							redirect_uri = default_schema + redirect_uri.substring("https".length());
						} else if(redirect_uri.toLowerCase().startsWith("http")) {
							redirect_uri = default_schema + redirect_uri.substring("http".length());
						}
					}
					result = new Result().setRedirectResource(redirect_uri);
				}
			}
			RunnerResult<T> ret_result = new RunnerResult<>();
			ret_result.setResult(Result.convert(result));
			ret_result.setId(id().id());
			ret_result.setCode(id().code());
			ret_result.setPath(this.transferPathToClz(req));
			ret_result.setAppins(this.appins);
			return ret_result;
		} catch (IllegalAccessException e) {
			throw new PrivilegeException(id().id(), id().code(), executor==null?null:executor.getName(), this.transferPathToClz(req), "SCRIPT_PRIVILEGE_ERROR", e);
		} catch (InstantiationException nfex) {
			throw new CompileException(id().id(), id().code(), executor==null?null:executor.getName(), this.transferPathToClz(req), "PROGRAM_COMPILE_ERROR", nfex);
		} catch (ClassNotFoundException nfex) {
			throw new Code404Exception(id().id(), id().code(), executor==null?null:executor.getName(), this.transferPathToClz(req), "CLASS_NOT_FOUND", nfex);
		} catch (Exception ex) {
			logger.error("", ex);
			throw new Code500Exception(id().id(), id().code(), executor==null?null:executor.getName(), this.transferPathToClz(req), "RUNNING_ERROR:" + ex.getCause(), ex);
		}
	}

	@Override
	public void onStart() {
		synchronized (_LOCK_) {
			if (!state.equals(STATE_INITED)) {
				throwCause(null);
			}
			state = STATE_STARTING;
			this.initByRes();
			state = STATE_RUNNING;
		}
	}

	@Override
	public JCLoader getManagerClassLoader() {
		return rootLoader;
	}

	public void bindBaseEnv(BootClassLoader envloader) {
		if (rootLoader == null) {
			synchronized (_LOCK_) {
				this.rootLoader = envloader;
			}
		}
	}

	protected void initByRes() {
		ContainerContextEnv.setCurrentContainer(this);
		String init_script = appins.getOrg() + "." + appins.getDever() + "." + appins.getCode() + "." + Common.INITIAL;
		try {
			Class<?> executor = this.getSignedClassLoader().getManaged().findClass(init_script);
			Binding context = new Binding();
			Script script = (Script) executor.newInstance();
			script.setBinding(context);
			Object result = script.run();
			logger.info("ID:"+ appins.getId() + "(CODE:" + appins.getCode() + ") INIT SUCCESS. Result=" + result);
		} catch (ClassNotFoundException e) {
			//e.printStackTrace();
			logger.info("ID:"+ appins.getId() + "(CODE:" + appins.getCode() + ") DOES NOT NEED TO INIT. FOR NOT SET INIT PROGRAM: " + init_script);
		} catch (Exception e) {
			logger.error("ID:"+ appins.getId() + "(CODE:" + appins.getCode() + ") INIT ERROR.", e);
		} finally {
			//JCAPPHolder.clearContainer();
			ContainerContextEnv.clearCurrentContainer();
		}
	}
	
	@Override
	public void onInit() {
		synchronized (_LOCK_) {
			if (!state.equals(STATE_READY)) {
				throwCause(null);
			}
			containClassLoader = new TypeDefClassLoader(rootLoader, this);
			//Thread.currentThread().setContextClassLoader(containClassLoader);	//SDK CONTENT INITED
			String appPath = appins.getApp_base();
			DevConfigureReader.init(appPath, appins.code);
			DevPowerLoader.init(appins.code);
			
			DatabasePowerHandler jcDatabasePower = (DatabasePowerHandler) PowerHandlerFactory.getPowerHandler(PowerName.DATABASE, appins.code);
			MemPowerHandler memPowerHandler = (MemPowerHandler) PowerHandlerFactory.getPowerHandler(PowerName.MEM, appins.code);
			QiniuPowerHandler qiniuPowerHandler = (QiniuPowerHandler) PowerHandlerFactory.getPowerHandler(PowerName.QINIU, appins.code);
			
			if(jcDatabasePower!=null) {
				BC_CAPS.add(Common.DATABASE, jcDatabasePower);
			}
			if(memPowerHandler!=null) {
				BC_CAPS.add(Common.MEM_POWER, memPowerHandler);
			}
			if(qiniuPowerHandler!=null) {
				BC_CAPS.add(Common.QINIU_POWER, qiniuPowerHandler);
			}
			
			state = STATE_INITED;
		}
	}

	@Override
	public void onStop() {
		//close thread task
		this.offTask();
	}

	@Override
	public void onDestroy() {
		this.offTask();
		appins = null;
		interceptorMap = null;
		configs = null;
		rootLoader = null;
	}

	protected void throwCause(String msg) {
		if (msg == null) {
			msg = "CONTAINER STATUS INVALID";
		}
		throw new RuntimeException(msg);
	}
}
