package com.jeancoder.root.container.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeancoder.core.Interceptor.IntercepHanRule;
import com.jeancoder.core.Interceptor.Interceptor;
import com.jeancoder.core.common.Common;
import com.jeancoder.core.http.JCRequest;
import com.jeancoder.core.http.JCResponse;
import com.jeancoder.core.http.JCThreadLocal;
import com.jeancoder.core.result.Result;
import com.jeancoder.root.container.ContainerContextEnv;
import com.jeancoder.root.container.JCAppContainer;
import com.jeancoder.root.container.PowerCaps;
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
import com.jeancoder.root.io.line.HeaderNames;

import groovy.lang.Binding;
import groovy.lang.Script;
import io.netty.handler.codec.http.HttpResponseStatus;

@SuppressWarnings("serial")
public abstract class DefaultContainer extends QContainer implements JCAppContainer {

	private static Logger logger = LoggerFactory.getLogger(DefaultContainer.class.getName());
	
	protected JCAPP appins;
	
	protected final PowerCaps BC_CAPS = new PowerCaps();
	
	protected List<Interceptor> interceptorMap = new ArrayList<>();
	
	protected Map<String, String> configs = new HashMap<>();
	
	public void addConfig(String filename, String content) {
		configs.put(filename, content);
	}
	
	public String getConfig(String filename) {
		return configs.get(filename);
	}
	
	@Override
	public JCAPP getApp() {
		return appins;
	}

	@Override
	public PowerCaps getCaps() {
		return BC_CAPS;
	}

	@Override
	public void addInterceptor(Interceptor interceptor) {
		String pre_clz = null;
		if(interceptor.getPreResource()!=null) {
			pre_clz = appins.getOrg() + "." + appins.getDever() + "." + appins.getCode() + "." + Common.INTERCEPTOR + "." + interceptor.getPreResource().replace("/", ".");
		}
		String pos_clz = null;
		if(interceptor.getPostResource()!=null) {
			pos_clz = appins.getOrg() + "." + appins.getDever() + "." + appins.getCode() + "." + Common.INTERCEPTOR + "." + interceptor.getPostResource().replace("/", ".");
		}
		//test to find
		if(pre_clz!=null) {
			try {
				Class<?> pre_clz_obj = this.getSignedClassLoader().getManaged().findClass(pre_clz);
				Object handler = pre_clz_obj.newInstance();
				if(logger.isDebugEnabled()) {
					logger.debug("pre interceptor test success:" + pre_clz_obj);
				}
				String source_path = appins.getApp_base() + "/" + appins.getSource_base() + "/" + pre_clz.replace(".", "/");
				IntercepHanRule rules = parseIntercepRules(source_path);
				interceptor.setHanRules(rules);
				interceptor.setPreResource(pre_clz);
				interceptor.bindHandler(handler);
			} catch(ClassNotFoundException | IllegalAccessException | InstantiationException clex) {
				interceptor.setPreResource(null);
			}
		}
		if(pos_clz!=null) {
			try {
				Class<?> pos_clz_obj = this.getSignedClassLoader().getManaged().findClass(pos_clz);
				if(logger.isDebugEnabled()) {
					logger.debug("post interceptor test success:" + pos_clz_obj);
				}
				interceptor.setPostResource(pos_clz);
			} catch(ClassNotFoundException clex) {
				interceptor.setPostResource(null);
			}
		}
		
		if(interceptor.getPreResource()!=null||interceptor.getPostResource()!=null)
			interceptorMap.add(interceptor);
	}
	
	public Enumeration<Interceptor> interceptors() {
		Vector<Interceptor> vec = new Vector<>();
		if(!interceptorMap.isEmpty()) {
			interceptorMap.forEach(k -> {
				vec.add(k);
			});
		}
		return vec.elements();
	}
	
	protected String transferPathToClz(JCHttpRequest req) {
		String app_context_path = req.getContextPath();
		String req_uri = req.getPathInfo();
		if(req_uri.equals("/")) {
			//req_uri = req_uri + "/welcome";		//set default index page
			//做一次rewrite 
			if(this.appins.getConfig().getIndex()!=null) {
				req_uri = this.appins.getConfig().getIndex();
			} else {
				req_uri = "welcome";
			}
		} else {
			req_uri = req_uri.substring(1);
		}
//		String app_action_path = req_uri.substring(app_context_path.length() + 1);
		String app_action_path = req_uri;
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
		String prefix = appins.getOrg() + "." + appins.getDever() + "." + appins.getCode() + "." + Common.INTERNAL;
		if(!path.startsWith("/")) {
			path = "/" + path;
		}
		prefix = prefix + path.replace('/', '.');
		return cutTailDotChar(prefix);
	}
	
	protected Class<?>  transferPathToIns(String path) throws ClassNotFoundException {
		String class_name = transferPathToClz(path);
		Class<?> executor = containClassLoader.getAppClassLoader().findClass(class_name);
		return executor;
	}
	
	public <T extends Result> RunnerResult<T> execute(String path) {
		Class<?> executor = null;
		JCAppContainer original_container = ContainerContextEnv.getCurrentContainer();
		ContainerContextEnv.setCurrentContainer(this);
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
		} finally {
			if(original_container!=null) {
				ContainerContextEnv.setCurrentContainer(original_container);
			} else {
				ContainerContextEnv.clearCurrentContainer();
			}
		}
	}
	
	protected TypeDefClassLoader containClassLoader = null;
	
	private static class InnerExchange {
//		public String entry;
//		
//		public List<String> intercep;
		
		public Object result;
		
		public InnerExchange(String uri, List<String> lastones, Object runres) {
//			this.entry = uri;
//			this.intercep = lastones;
			this.result = runres;
		}
		
		public boolean isSuccess() {
			return (result instanceof Boolean)?(Boolean)result:false;
		}
	}
	
	protected InnerExchange callInterceptor(JCHttpRequest req, JCHttpResponse res) {
		Enumeration<Interceptor> inters = this.interceptors();
		List<String> passed = new ArrayList<>();
		if(inters!=null) {
			while(inters.hasMoreElements()) {
				Interceptor its = inters.nextElement();
				String resname = its.getPreResource();
				String servlet_path = req.getRequestURI().substring(("/" + appins.getCode()).length());
				if(its.canExecute(servlet_path)&&its.getHandler()!=null) {
					try {
						//Class<?> executor = containClassLoader.getAppClassLoader().findClass(resname);
						//Script script = (Script) executor.newInstance();
						Script script = (Script) its.getHandler();
						Binding context = new Binding();
						script.setBinding(context);
						Object result = script.run();
						passed.add(resname);
						
						if(result==null || ((result instanceof Boolean)&&(!(boolean)result))) {
							// try get from thread vars
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
						if(!(result instanceof Boolean)) {
							return new InnerExchange(req.getRequestURI(), passed, result);
						}
						boolean run_result = (boolean)result;
						if(!run_result) {
							return new InnerExchange(req.getRequestURI(), passed, run_result);
						}
					} 
//					catch (IllegalAccessException e) {
//						throw new PrivilegeException(id().id(), id().code(), resname, this.transferPathToClz(req), "SCRIPT_PRIVILEGE_ERROR", e);
//					} catch (InstantiationException nfex) {
//						throw new CompileException(id().id(), id().code(), resname, this.transferPathToClz(req), "PROGRAM_COMPILE_ERROR", nfex);
//					} catch (ClassNotFoundException nfex) {
//						throw new Code404Exception(id().id(), id().code(), resname, this.transferPathToClz(req), "CLASS_NOT_FOUND", nfex);
//					} 
					catch (Exception ex) {
						ex.printStackTrace();
						throw new Code500Exception(id().id(), id().code(), resname, this.transferPathToClz(req), "RUNNING_ERROR:" + ex.getCause(), ex);
					}
				}   
			}
		}
		return new InnerExchange(req.getRequestURI(), passed, true);
	}
	
	@Override
	public final <T extends Result> RunnerResult<T> callEntry(JCHttpRequest req, JCHttpResponse res) {
//		String servlet_path = req.getRequestURI().substring(("/" + appins.getCode()).length());
		String servlet_path = req.getPathInfo();
		if(servlet_path.startsWith("/" + appins.getSta_base())) {
			//说明是静态资源
			Result result = new Result().setStaticName(servlet_path.substring(("/" + appins.getSta_base()).length() + 1));
			RunnerResult<T> ret_result = new RunnerResult<>();
			ret_result.setResult(Result.convert(result));
			ret_result.setId(id().id());
			ret_result.setCode(id().code());
			ret_result.setPath(this.transferPathToClz(req));
			ret_result.setAppins(this.appins);
			return ret_result;
		}
		JCThreadLocal.setRequest(new JCRequest(req));
		JCThreadLocal.setResponse(new JCResponse(res));
		
		//JCAPPHolder.setContainer(this);
		ContainerContextEnv.setCurrentContainer(this);
		try {
			InnerExchange incsr = this.callInterceptor(req, res);
			if(incsr.isSuccess()) {
				RunnerResult<T> result = this.run(req, res);
				return result;
			} else {
				RunnerResult<T> ret_result = new RunnerResult<>();
				ret_result.setResult(Result.convert(incsr.result));
				ret_result.setId(id().id());
				ret_result.setCode(id().code());
				ret_result.setPath(this.transferPathToClz(req));
				ret_result.setAppins(this.appins);
				return ret_result;
			}
		} catch(Exception e) {
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
		} finally {
			ContainerContextEnv.clearCurrentContainer();
		}
	}

	private String cutTailDotChar(String kelxz) {
		if(!kelxz.endsWith(".")) {
			return kelxz;
		}
		return cutTailDotChar(kelxz.substring(0, kelxz.length() - 1));
	}
	
	protected abstract <T extends Result> RunnerResult<T> run(JCHttpRequest req, JCHttpResponse res);
	
	private IntercepHanRule parseIntercepRules(String path) {
		path = path + "." + appins.getLans();
		List<String> mapping = new ArrayList<>();
		List<String> exmapping = new ArrayList<>();
		try (Stream<String> stream = Files.lines(Paths.get(path))) {
			stream.forEach(
				line -> {
					line = line.trim();
					if(line.startsWith("@urlmapped")) {
						line = line.substring("@urlmapped(".length(), line.indexOf(")")).trim();
						for(String t : line.split(",")) {
							t = t.trim();
							t = disposeFirst(t, "[");
							t = disposeLast(t, "]");
							mapping.add(t.substring(1, t.length() - 1));
						}
					} else if(line.startsWith("@urlpassed")) {
						line = line.substring("@urlpassed(".length(), line.indexOf(")")).trim();
						for(String t : line.split(",")) {
							t = t.trim();
							t = disposeFirst(t, "[");
							t = disposeLast(t, "]");
							exmapping.add(t.substring(1, t.length() - 1));
						}
					}
				}
			);
		}catch (IOException e) {
			logger.error("", e);
		}
		if(mapping.isEmpty()) {
			//默认全部需要执行
			mapping.add("/");
		}
		mapping.forEach(it -> logger.info(it));
		exmapping.forEach(it -> logger.info(it));
		IntercepHanRule rule = new IntercepHanRule();
		rule.setMapping(mapping);
		rule.setExmapping(exmapping);
		return rule;
	}
	
	protected static String disposeLast(String uri, String charac) {
		uri = uri.trim();
		if(uri.equals(charac)) {
			return uri;
		}
		if(uri.endsWith(charac)) {
			uri = uri.substring(0, uri.length() - 1);
			if(uri.endsWith(charac)) {
				uri = disposeLast(uri, charac);
			}
		}
		return uri;
	}
	
	protected static String disposeFirst(String uri, String charac) {
		uri = uri.trim();
		if(uri.equals(charac)) {
			return uri;
		}
		if(uri.startsWith(charac)) {
			uri = uri.substring(1, uri.length());
			if(uri.startsWith(charac)) {
				uri = disposeFirst(uri, charac);
			}
		}
		return uri;
	}
	
}
