package com.jeancoder.core.resource.proc;

import java.util.LinkedHashMap;
import java.util.Map;

import com.jeancoder.core.namer.NamerApplication;

public class Application {
	
	private NamerApplication app;
	
	private ClassLoader classLoader;
	
	private Map<String, ResourceBundle> bundlesMap = new LinkedHashMap<String, ResourceBundle>();
	
	public Resource getResource(String type, String resId) {
		ResourceBundle resourceBundle = bundlesMap.get(type);
		if (resourceBundle == null) { 
			return null;
		}
		Resource ret = null;
		for (Resource r : resourceBundle.getResources()) {
			if (r.getResId().equals(resId)) {
				ret = r;
			}
		}
		return ret;
	}
	
	public String getAppId() {
		return app.getAppId();
	}
	
	public String getAppCode() {
		return app.getAppCode();
	}
	
	public void setApp(NamerApplication app){
		this.app = app;
	}

	public ResourceBundle getBundlesByType(String type) {
		return bundlesMap.get(type);
	}

	public void addBundles(String type, ResourceBundle bundles) {
		bundlesMap.put(type, bundles);
	}

	
	public NamerApplication getApp() {
		return app;
	}

	public Map<String, ResourceBundle> getBundlesMap() {
		return bundlesMap;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public void setBundlesMap(Map<String, ResourceBundle> bundlesMap) {
		this.bundlesMap = bundlesMap;
	}
}
