package com.jeancoder.root.vm;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.Cookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jc.VmPsp;
import com.jc.proto.conf.ServerMod;
import com.jeancoder.core.http.JCThreadLocal;
import com.jeancoder.core.rendering.Rendering;
import com.jeancoder.core.rendering.RenderingFactory;
import com.jeancoder.core.result.Result;
import com.jeancoder.core.result.ResultType;
import com.jeancoder.root.container.ContainerMaps;
import com.jeancoder.root.container.JCAppContainer;
import com.jeancoder.root.container.core.BCID;
import com.jeancoder.root.container.core.LifecycleZa;
import com.jeancoder.root.env.JCAPP;
import com.jeancoder.root.env.Lifecycle;
import com.jeancoder.root.env.RunnerResult;
import com.jeancoder.root.handler.RunnerResultListener;
import com.jeancoder.root.io.http.JCHttpRequest;
import com.jeancoder.root.io.http.JCHttpResponse;
import com.jeancoder.root.io.http.JCReqFaca;
import com.jeancoder.root.io.http.RequestFacade;
import com.jeancoder.root.manager.JCVMDelegator;

import io.netty.channel.ChannelHandlerContext;

public abstract class DefaultVm extends LifecycleZa implements JCVM {

	private static Logger logger = LoggerFactory.getLogger(DefaultVm.class.getName());
	
	protected static volatile String state = STATE_READY;
	
	public final ContainerMaps VM_CONTAINERS = new ContainerMaps();
	
	protected List<JCAPP> appList;
	
	protected String sysLibs = null;
	
	protected VmPsp vmconf;
	
	@Override
	public void bindLibrary(String lib_path) {
		this.sysLibs = lib_path;
	}

	@Override
	public ContainerMaps getContainers() {
		return VM_CONTAINERS;
	}

	public void initVMPS(ServerMod mod) {
		this.vmconf = VmPsp.build(mod);
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
		String content_type = req.getContentType();
		
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
			logger.debug("CONTENT TYPE=" + content_type);
		}
		
		ChannelHandlerContext ctx = JCVMDelegator.getContext().getContext();
		RunnerResult<T> exeresult = makeRun(req, res);
		try {
			Rendering rendering = RenderingFactory.getRendering(ctx, exeresult);
			Object data_obj = rendering.process(req, res);
			exeresult.setData(data_obj);
			
//			Object after = rendering.process(req, res);
			//logger.info("wait to dispose=" + after);
//			if(after!=null) {
//				afterTriggered(req, res, after);
//			}
		} catch (Exception e) {
			//logger.error(e.getMessage(), e);
			throw e;
		} finally {
			JCThreadLocal.clearRequest();
			JCThreadLocal.clearResponse();
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
		JCHttpRequest wrapped = this.wrapperRequestAndResponse(req, res);
		String app_context_path = wrapped.getContextPath();
		for(BCID app : getContainers().keySet()) {
			String app_code = app.code();
			if(app_context_path.equals("/" + app_code)) {
				JCAppContainer runner = getContainers().get(app);
				String state = runner.state();
				if(!state.equals(Lifecycle.STATE_RUNNING)) {
					continue;
				}
				RunnerResult<T> ret = runner.callEntry(wrapped, res);
				ret.addListener(new RunnerResultListener<Result>());
				return ret;
			}
		}
		logger.error("can not find the cooresponding container by :" + req.getRequestURI());
		//throw new SPPEmptyException(app_context_path.substring(1), app_context_path.substring(1), req.getRequestURI(), req.getRequestURI(), "", null);
		return null;
	}
	
	protected void afterTriggered(JCHttpRequest req, JCHttpResponse res, Object result) {
		if((result instanceof Result)&&((Result)result).getResultType().equals(ResultType.CONTROLLER_RESOURCE)) {
			QueryUriDis decoder = new QueryUriDis(((Result)result).getResult());
			
			req.setAttribute(JCReqFaca.FORWARD_REQUEST_CONTEXT, decoder.getAppCode());
			req.setAttribute(JCReqFaca.FORWARD_REQUEST_FULLURI, decoder.getRequestURI());
			req.setAttribute(JCReqFaca.FORWARD_REQUEST_PATH, decoder.getPath());
			req.setAttribute(JCReqFaca.FORWARD_REQUEST_QUERY, decoder.getQueryString());
			
			JCHttpRequest warrper;
			try {
				warrper = new RequestFacade(req);
				JCVMDelegatorGroup.instance().getDelegator().getVM().dispatch(warrper, res);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected JCHttpRequest wrapperRequestAndResponse(JCHttpRequest req, JCHttpResponse res) {
		String context_path = req.getContextPath();
		
		JCHttpRequest wrapperReq = req;
		if(!needWrappedRequest(req)) {
			return req;
		}
		String hostOnlyVisitPath = this.vmconf.getHostOnlyDisp();
		QueryUriDis decoder = new QueryUriDis(hostOnlyVisitPath);
		if(decoder.getRequestURI().equals(context_path)) {
			this.processEqualPathForwardException(req);
		}
		req.setAttribute(JCReqFaca.FORWARD_REQUEST_CONTEXT, decoder.getAppCode());
		req.setAttribute(JCReqFaca.FORWARD_REQUEST_FULLURI, decoder.getRequestURI());
		req.setAttribute(JCReqFaca.FORWARD_REQUEST_PATH, decoder.getPath());
		req.setAttribute(JCReqFaca.FORWARD_REQUEST_QUERY, decoder.getQueryString());
		
		try {
			return new RequestFacade(wrapperReq);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	protected void processEqualPathForwardException(JCHttpRequest req) {
		throw new RuntimeException("FORWARD EXCEPTION FOR EQUALS");
	}
	
	private boolean needWrappedRequest(JCHttpRequest req) {
		String context_path = req.getContextPath();
		if(context_path==null || context_path.equals("/") || context_path.equals("")) {
			return true;
		}
		return false;
	}
	
	static class QueryUriDis {
		String uri;
		
		QueryUriDis(String uri ) {
			if(uri==null||uri.trim().equals("")) {
				uri = "/";
			}
			this.uri = uri.trim();
		}
		
		String getAppCode() {
			String uri = this.uri;
			if (uri.length() > 1) {
				StringBuffer buff = new StringBuffer();
				int start = 0;
				for (;;) {
					if (start >= uri.length()) {
						break;
					}
					char c;
					if ((c = uri.charAt(start++)) != '/' && c != '?' && c != '#') {
						buff.append(c + "");
					} else {
						if (buff.length() > 0) {
							break;
						}
					}
				}
				buff.insert(0, '/');
				return buff.toString();
			} else {
				return "/";
			}
		}
		
		String getRequestURI() {
			if (uri.indexOf('?') == -1) {
				return uri;
			} else {
				return uri.substring(0, uri.indexOf('?'));
			}
		}
		
		String getQueryString() {
			if (uri.indexOf('?') == -1) {
				return "";
			} else {
				return uri.substring(uri.indexOf('?') + 1);
			}
		}
		
		String getPath() {
			if(getRequestURI().equals(getAppCode())) {
				return "/";
			}
			return getRequestURI().substring(getAppCode().length());
		}
	}
}


