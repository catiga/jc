package com.jeancoder.root.vm;

import java.util.List;

import javax.servlet.http.Cookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeancoder.core.http.JCThreadLocal;
import com.jeancoder.core.rendering.Rendering;
import com.jeancoder.core.rendering.RenderingFactory;
import com.jeancoder.core.result.Result;
import com.jeancoder.root.container.ContainerMaps;
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
	
	public final ContainerMaps VM_CONTAINERS = new ContainerMaps();
	
	protected List<JCAPP> appList;
	
	protected String sysLibs = null;
	
	@Override
	public void bindLibrary(String lib_path) {
		this.sysLibs = lib_path;
	}

	@Override
	public ContainerMaps getContainers() {
		return VM_CONTAINERS;
	}

	@Override
	public void setInitApps(List<JCAPP> appList) {
		this.appList = appList;
	}

	@Override
	public String meId() {
		String vmid = JCVMDelegator.delegate().getVM().toString();
		vmid = vmid.substring(vmid.lastIndexOf(".") + 1);
		return vmid;
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
			logger.debug("SERVER DOMAIN=" + host + ":" + port);
			logger.debug("REMOTE HOST AND IP=" + vis_host_name + "(" + vis_host_ip + ")");
			logger.debug("CONTEXT TYPE=" + context_type);
		}
		
		ChannelHandlerContext ctx = JCVMDelegator.getContext().getContext();
		RunnerResult<T> exeresult = makeRun(req, res);
		try {
			Rendering rendering = RenderingFactory.getRendering(ctx, exeresult);
			rendering.process(req, res);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
//			JCThreadLocal.clearClassLoader();
			JCThreadLocal.clearRequest();
			JCThreadLocal.clearResponse();
//			JCThreadLocal.clearCode();
		}
		return exeresult;
	}
	
	/**
	 * only support run entry script
	 * @param req
	 * @param res
	 * @return
	 */
	protected <T extends Result> RunnerResult<T> makeRun(JCHttpRequest req, JCHttpResponse res) {
		String app_context_path = req.getContextPath();
		for(BCID app : getContainers().keySet()) {
			String app_code = app.code();
			if(app_context_path.equals("/" + app_code)) {
				JCAppContainer runner = getContainers().get(app);
				RunnerResult<T> ret = runner.callEntry(req, res);
				return ret;
			}
		}
		return null;
	}
	
}
