package com.jeancoder.root.container.loader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeancoder.core.cl.AppLoader;
import com.jeancoder.core.cl.DefLoader;
import com.jeancoder.core.cl.JCLoader;
import com.jeancoder.core.common.Common;
import com.jeancoder.root.container.JCAppContainer;
import com.jeancoder.root.env.JCAPP;
import com.jeancoder.root.state.JCAPPHolder;

import groovy.lang.Binding;
import groovy.lang.Script;

/**
 * app lib loader
 * @author jackielee
 *
 */
public class TypeDefClassLoader extends URLClassLoader implements DefLoader, JCLoader {

	private static Logger logger = LoggerFactory.getLogger(TypeDefClassLoader.class);
	
	BootClassLoader parent = null;
	
	AppClassLoader appClassLoader = null;
	
	JCAPP appins;
	
	@Override
	public AppLoader getManaged() {
		return appClassLoader;
	}

	private JCAppContainer container;
	
	public TypeDefClassLoader(BootClassLoader parent, JCAppContainer container) {
		super(EMPTY_URL_ARRAY, parent);
		this.parent = parent;
//		this.appins = appins;
		this.container = container;
		this.appins = container.getApp();
		registerSysJars(appins.getApp_base() + "/" + appins.getLib_base());
		this.appClassLoader = new AppClassLoader(this);
		initByRes();
	}
	
	protected void initByRes() {
		JCAPPHolder.setContainer(container);	//not goods way, fuck
		try {
			Class<?> executor = appClassLoader.findClass(appins.getOrg() + "." + appins.getDever() + "." + appins.getCode() + "." + Common.INITIAL);
			Binding context = new Binding();
			Script script = (Script) executor.newInstance();
			script.setBinding(context);
			Object result = script.run();
			logger.info("ID:"+ appins.getId() + "(CODE:" + appins.getCode() + ") INIT SUCCESS. Result=" + result);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			logger.error("ID:"+ appins.getId() + "(CODE:" + appins.getCode() + ") INIT ERROR.", e);
		} finally {
			JCAPPHolder.clearContainer();
		}
	}
	
	public TypeDefClassLoader(URL[] urls, BootClassLoader parent) {
		super(urls, parent);
		this.parent = parent;
	}

	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException {
		Class<?> claz = findLoadedClass(name);
		if(claz==null) {
			try {
				claz = super.findClass(name);
			}catch(ClassNotFoundException not) {}
		}
		if(claz==null) {
			claz = parent.findClass(name);
		}
		return claz;
	}
	
	public void setParent(BootClassLoader parent) {
		this.parent = parent;
	}

	public AppClassLoader getAppClassLoader() {
		return appClassLoader;
	}

	public void setAppClassLoader(AppClassLoader appClassLoader) {
		this.appClassLoader = appClassLoader;
	}
	
	public void countPathUrls(String path, List<URL> waiting) {
		File basePath = new File(path);
	    if(basePath.exists()) {
	    	if(basePath.isDirectory()) {
		    	File[] files = basePath.listFiles();
		    	for(File f : files) {
		    		countPathUrls(f.getPath(), waiting);
		    	}
	    	} else {
		    	try {
	    			waiting.add(basePath.toURI().toURL());
	    		} catch(IOException ioex) {
	    		}
		    }
	    }
	}
	
	final public List<URL> registerSysJars(String jarPath) {
		List<URL> waiting = new ArrayList<>();
		countPathUrls(jarPath, waiting);
	    
	    Method method = null;
	    try {
	        method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
	    } catch (NoSuchMethodException | SecurityException e1) {
	        e1.printStackTrace();
	    }
	    boolean accessible = method.isAccessible();
	    try {
	        method.setAccessible(true);
	        for(URL jarFile : waiting) {
		        method.invoke(this, jarFile);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        method.setAccessible(accessible);
	    }
	    
	    return waiting;
	}

	public JCAPP getAppins() {
		return appins;
	}

}
