package com.jeancoder.root.container.core;

import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeancoder.root.container.JCAppContainer;
import com.jeancoder.root.container.JCVM;
import com.jeancoder.root.container.model.JCAPP;
import com.jeancoder.root.io.http.JCHttpRequest;

public abstract class DefaultVm extends LifecycleZa implements JCVM {

	private static Logger logger = LoggerFactory.getLogger(DefaultVm.class);
	
	protected static volatile String state = STATE_READY;
	
	protected List<JCAPP> appList;
	
	@Override
	public Map<String, JCAppContainer> getContainers() {
		return JCVM.VM_CONTAINERS;
	}

	@Override
	public void setInitApps(List<JCAPP> appList) {
		this.appList = appList;
	}

	@Override
	public void dispatch(JCHttpRequest req) {
		// TODO Auto-generated method stub
		logger.info("execute ......");
		Object obj = req.getParameterMap();
		logger.info(obj.toString());
		Cookie[] cookies = req.getCookies();
		String path_info = req.getPathInfo();
		String query_string = req.getQueryString();
		String request_uri = req.getRequestURI();
		String context_path = req.getContextPath();
		String context_type = req.getContentType();
		
		String host = req.getLocalAddr();
		int port = req.getLocalPort();
		
		String vis_host_name = req.getRemoteHost();
		String vis_host_ip = req.getRemoteAddr();
		
		System.out.println(req.getRequestURL());
		System.out.println(query_string);
		
		Object exeresult = null;
		for(String app : getContainers().keySet()) {
			String app_code = app.substring(app.indexOf(":") + 1);
			if((context_path).equals("/" + app_code)) {
				JCAppContainer runner = getContainers().get(app);
				exeresult = runner.execute(req);
				break;
			}
		}
		System.out.println(exeresult);
	}
	
}
