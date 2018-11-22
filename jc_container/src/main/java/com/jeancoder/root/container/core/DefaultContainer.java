package com.jeancoder.root.container.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeancoder.core.common.Common;
import com.jeancoder.core.http.JCRequest;
import com.jeancoder.core.http.JCResponse;
import com.jeancoder.core.http.JCThreadLocal;
import com.jeancoder.core.result.Result;
import com.jeancoder.root.container.JCAppContainer;
import com.jeancoder.root.container.loader.TypeDefClassLoader;
import com.jeancoder.root.env.JCAPP;
import com.jeancoder.root.env.RunnerResult;
import com.jeancoder.root.exception.Code404Exception;
import com.jeancoder.root.exception.Code500Exception;
import com.jeancoder.root.exception.CompileException;
import com.jeancoder.root.exception.PrivilegeException;
import com.jeancoder.root.exception.RunningException;
import com.jeancoder.root.io.http.JCHttpRequest;
import com.jeancoder.root.io.http.JCHttpResponse;

import groovy.lang.Binding;
import groovy.lang.Script;

public abstract class DefaultContainer extends LifecycleZa implements JCAppContainer {

	private static Logger logger = LoggerFactory.getLogger(DefaultContainer.class);
	
	protected JCAPP appins;
	
	protected String transferPathToClz(JCHttpRequest req) {
		String app_context_path = req.getContextPath();
		String app_action_path = req.getRequestURI().substring(app_context_path.length() + 1);
		String prefix = appins.getOrg() + "." + appins.getDever() + ".";
		prefix = prefix + app_context_path.substring(1) + "." + Common.ENTRY + "." + app_action_path.replace('/', '.');
		return cutTailDotChar(prefix);
	}
	
	protected Class<?>  transferPathToIns(JCHttpRequest req) throws ClassNotFoundException {
		String class_name = transferPathToClz(req);
		Class<?> executor = containClassLoader.getAppClassLoader().findClass(class_name);
		return executor;
	}
	
	protected String transferPathToClz(String path) {
		String prefix = appins.getOrg() + "." + appins.getDever() + ".";
		if(!path.startsWith("/")) {
			path += "/";
		}
		prefix = prefix + path.substring(1) + "." + Common.INTERNAL + "." + path.replace('/', '.');
		return cutTailDotChar(prefix);
	}
	
	protected Class<?>  transferPathToIns(String path) throws ClassNotFoundException {
		String class_name = transferPathToClz(path);
		Class<?> executor = containClassLoader.getAppClassLoader().findClass(class_name);
		return executor;
	}
	
	public <T extends Result> RunnerResult<T> execute(String path) {
		JCThreadLocal.setClassLoader(containClassLoader.getAppClassLoader());
		JCThreadLocal.setCode(appins.getCode());
		Class<?> executor = null;
		try {
			executor = this.transferPathToIns(path);
			Binding context = new Binding();
			Script script = (Script) executor.newInstance();
			script.setBinding(context);
			Object result = script.run();
			RunnerResult<T> ret_result = new RunnerResult<>();
			ret_result.setResult(Result.convert(result));
			ret_result.setId(id().id());
			ret_result.setCode(id().code());
			ret_result.setPath(this.transferPathToClz(path));
			ret_result.setAppins(this.appins);
			return ret_result;
		} catch (IllegalAccessException e) {
			throw new PrivilegeException(id().id(), id().code(), executor==null?null:executor.getName(), this.transferPathToClz(path), "SCRIPT_PRIVILEGE_ERROR", e);
		} catch (InstantiationException nfex) {
			throw new CompileException(id().id(), id().code(), executor==null?null:executor.getName(), this.transferPathToClz(path), "PROGRAM_COMPILE_ERROR", nfex);
		} catch (ClassNotFoundException nfex) {
			throw new Code404Exception(id().id(), id().code(), executor==null?null:executor.getName(), this.transferPathToClz(path), "CLASS_NOT_FOUND", nfex);
		} catch (Exception ex) {
			logger.error("", ex);
			throw new Code500Exception(id().id(), id().code(), executor==null?null:executor.getName(), this.transferPathToClz(path), "RUNNING_ERROR:" + ex.getCause(), ex);
		}
	}
	
	protected TypeDefClassLoader containClassLoader = null;
	
	@Override
	public final <T extends Result> RunnerResult<T> execute(JCHttpRequest req, JCHttpResponse res) {
		JCThreadLocal.setClassLoader(containClassLoader.getAppClassLoader());
		JCThreadLocal.setRequest(new JCRequest(req));
		JCThreadLocal.setResponse(new JCResponse(res));
		JCThreadLocal.setCode(appins.getCode());
		try {
			RunnerResult<T> result = this.run(req, res);
			return result;
		}catch(Exception e) {
			if(e instanceof RunningException) {
				throw e;
			} else {
				logger.error("", e);
				Class<?> resobj = null;
				try {
					resobj = this.transferPathToIns(req);
				} catch(ClassNotFoundException clex) {
				}
				throw new Code500Exception(appins, resobj==null?null:resobj.getName(), this.transferPathToClz(req), "RUNNING_ERROR:", e);
			}
		}
	}

	private String cutTailDotChar(String kelxz) {
		if(!kelxz.endsWith(".")) {
			return kelxz;
		}
		return cutTailDotChar(kelxz.substring(0, kelxz.length() - 1));
	}
	
	public abstract <T extends Result> RunnerResult<T> run(JCHttpRequest req, JCHttpResponse res);
}
