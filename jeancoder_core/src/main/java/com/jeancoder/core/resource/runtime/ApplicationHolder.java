package com.jeancoder.core.resource.runtime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.jeancoder.core.common.Common;
import com.jeancoder.core.namer.NamerApplication;
import com.jeancoder.core.resource.proc.Application;
import com.jeancoder.core.resource.proc.Resource;
import com.jeancoder.core.resource.proc.ResourceBundle;

public class ApplicationHolder {

	private static ApplicationHolder _instance = null;
	
	private ApplicationHolder() {
	}
	
	//need to build from cache server
	private Map<String, Application> _runtime_applications_ = new LinkedHashMap<>();
	
	public static ApplicationHolder getInstance() {
		if(_instance==null) {
			synchronized(ApplicationHolder.class) {
				if(_instance==null) {
					_instance = new ApplicationHolder();
				}
			}
		}
		return _instance;
	}
	
	public void addApp(Application app) {
		_runtime_applications_.put(app.getAppCode(), app);
	}
	
	public Application getAppByCode(String code) {
		if (_runtime_applications_ != null) {
			return _runtime_applications_.get(code);
		} else {
			return null;
		}
	}

	public void removeApp(String appCode) {
		 _runtime_applications_.remove(appCode);
	}
	public List<NamerApplication> getAll() {
		List<NamerApplication> list = new ArrayList<NamerApplication>();
		Iterator<Entry<String, Application>>  applicationMap = _runtime_applications_.entrySet().iterator();
		while (applicationMap.hasNext()) {  
			Map.Entry<String,Application> entry = applicationMap.next();
			list.add(entry.getValue().getApp());
	    }  
		return list;
	}
	
	
	public void prinAll() {
		Iterator<Entry<String, Application>>  applicationMap = _runtime_applications_.entrySet().iterator();
		while (applicationMap.hasNext()) {  
			Map.Entry<String,Application> entry = applicationMap.next();
			System.out.println("appId：" + entry.getKey());
			Iterator<Entry<String, ResourceBundle>> resourceBundleMap = entry.getValue().getBundlesMap().entrySet().iterator();
			while (resourceBundleMap.hasNext()) {  
				Map.Entry<String,ResourceBundle> resourceBundleEntry = resourceBundleMap.next();  
				System.out.println("ResourceBundleType：" + resourceBundleEntry.getKey());
				for (Resource resources : resourceBundleEntry.getValue().getResources()) {
					if (resources == null) {
						continue;
					}
					System.out.println("	Rescontent=" + resources.getRescontent() + " " + "ResId =" + resources.getResId());
				}
		    }  
	    }  
	}
	 
	public void prinEntry(String appCode) {
		Application application = _runtime_applications_.get(appCode);
		if (application == null) {
			System.out.println(appCode + "is null");
		}
		Iterator<Entry<String, ResourceBundle>> resourceBundleMap = application.getBundlesMap().entrySet().iterator();
		while (resourceBundleMap.hasNext()) {  
			Map.Entry<String,ResourceBundle> resourceBundleEntry = resourceBundleMap.next();  
			System.out.println("ResourceBundleType：" + resourceBundleEntry.getKey());
			if (!Common.ENTRY.equals(resourceBundleEntry.getKey())) {
				continue;
			}
			for (Resource resources : resourceBundleEntry.getValue().getResources()) {
				if (resources == null) {
					continue;
				}
				System.out.println("	Rescontent=" + resources.getRescontent() + " " + "ResId =" + resources.getResId());
			}
	    }  
	}
}
