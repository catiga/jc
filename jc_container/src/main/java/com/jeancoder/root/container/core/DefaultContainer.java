package com.jeancoder.root.container.core;

import com.jeancoder.root.container.JCAppContainer;
import com.jeancoder.root.container.loader.TypeDefClassLoader;
import com.jeancoder.root.io.http.JCHttpRequest;

public abstract class DefaultContainer extends LifecycleZa implements JCAppContainer {

	protected String transferPathToClz(JCHttpRequest req) {
		String app_context_path = req.getContextPath();
		String app_action_path = req.getRequestURI().substring(app_context_path.length() + 1);
		return "com.jeancoder." + app_context_path.substring(1) + ".entry." + app_action_path.replace('/', '.');
	}
	
	protected Class<?>  transferPathToIns(JCHttpRequest req) throws ClassNotFoundException {
		String class_name = transferPathToClz(req);
		Class<?> executor = containClassLoader.getAppClassLoader().findClass(class_name);
		return executor;
	}
	
	protected TypeDefClassLoader containClassLoader = null;
	
}
