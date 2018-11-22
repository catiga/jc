package com.jeancoder.root.vm;

import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeancoder.app.sdk.rendering.RenderingFactory;
import com.jeancoder.core.rendering.Rendering;
import com.jeancoder.core.result.Result;
import com.jeancoder.root.container.JCAppContainer;
import com.jeancoder.root.container.core.BCID;
import com.jeancoder.root.container.core.LifecycleZa;
import com.jeancoder.root.env.JCAPP;
import com.jeancoder.root.env.RunnerResult;
import com.jeancoder.root.io.http.JCHttpRequest;
import com.jeancoder.root.io.http.JCHttpResponse;
import com.jeancoder.root.manager.JCVMDelegator;

import io.netty.channel.ChannelHandlerContext;

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
	public <T extends Result> RunnerResult<T> dispatch(JCHttpRequest req, JCHttpResponse res) {
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
		
		if(logger.isDebugEnabled()) {
			logger.debug("COOKIES=" + cookies.toString());
			logger.debug("REQUEST URI=" + request_uri);
			logger.debug("CONTEXT PATH=" + context_path);
			logger.debug("PATH INFO=" + path_info);
			logger.debug("QUERY STRING=" + query_string);
		}
		logger.info("SERVER DOMAIN=" + host + ":" + port);
		logger.info("REMOTE HOST AND IP=" + vis_host_name + "(" + vis_host_ip + ")");
		logger.info("CONTEXT TYPE=" + context_type);
		
		RunnerResult<T> exeresult = makeRun(req, res);
		
		ChannelHandlerContext ctx = JCVMDelegator.getContext().getContext();
		Rendering rendering = RenderingFactory.getRendering(ctx, exeresult);
		try {
			rendering.process(req, res);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return exeresult;
	}
	
	protected <T extends Result> RunnerResult<T> makeRun(JCHttpRequest req, JCHttpResponse res) {
		String app_context_path = req.getContextPath();
		for(BCID app : getContainers().keySet()) {
			String app_code = app.code();
			if(app_context_path.equals("/" + app_code)) {
				JCAppContainer runner = getContainers().get(app);
				RunnerResult<T> ret = runner.execute(req, res);
				return ret;
			}
		}
		return null;
	}
	
}
