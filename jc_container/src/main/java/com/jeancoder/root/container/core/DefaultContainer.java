package com.jeancoder.root.container.core;

import com.jeancoder.core.common.Common;
import com.jeancoder.root.container.JCAppContainer;
import com.jeancoder.root.container.loader.TypeDefClassLoader;
import com.jeancoder.root.env.JCAPP;
import com.jeancoder.root.io.http.JCHttpRequest;

public abstract class DefaultContainer extends LifecycleZa implements JCAppContainer {

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
	
	protected TypeDefClassLoader containClassLoader = null;
	
	private String cutTailDotChar(String kelxz) {
		if(!kelxz.endsWith(".")) {
			return kelxz;
		}
		return cutTailDotChar(kelxz.substring(0, kelxz.length() - 1));
	}
}
