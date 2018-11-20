package com.jeancoder.root.container.core;

import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeancoder.core.result.Result;
import com.jeancoder.root.container.JCAppContainer;
import com.jeancoder.root.container.JCVM;
import com.jeancoder.root.container.model.JCAPP;
import com.jeancoder.root.io.http.JCHttpRequest;
import com.jeancoder.root.io.http.JCHttpResponse;

public abstract class DefaultVm extends LifecycleZa implements JCVM {

	private static Logger logger = LoggerFactory.getLogger(DefaultVm.class);
	
	protected static volatile String state = STATE_READY;
	
	protected List<JCAPP> appList;
	
	@Override
	public Map<BCID, JCAppContainer> getContainers() {
		return JCVM.VM_CONTAINERS;
	}

	@Override
	public void setInitApps(List<JCAPP> appList) {
		this.appList = appList;
	}

	@Override
	public Result dispatch(JCHttpRequest req, JCHttpResponse res) {
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
		
		Object exeresult = makeRun(req);
		if(!(exeresult instanceof Result)) {
			Result result = new Result();
			result.setData(exeresult);
			return result;
		}
		return (Result)exeresult;
	}
	
	protected <T> T makeRun(JCHttpRequest request) {
		String app_context_path = request.getContextPath();
		for(BCID app : getContainers().keySet()) {
			String app_code = app.code();
			if(app_context_path.equals("/" + app_code)) {
				JCAppContainer runner = getContainers().get(app);
				return runner.execute(request);
			}
		}
		return null;
	}
}
