package com.jeancoder.app.sdk.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jeancoder.app.sdk.Interceptor.JCInterceptorChain;
import com.jeancoder.app.sdk.Interceptor.JCInterceptorStack;
import com.jeancoder.app.sdk.rendering.RenderingFactory;
import com.jeancoder.app.sdk.runner.Runner;
import com.jeancoder.core.common.Common;
import com.jeancoder.core.http.GatewayServlet;
import com.jeancoder.core.http.JCRequest;
import com.jeancoder.core.http.JCResponse;
import com.jeancoder.core.http.JCThreadLocal;
import com.jeancoder.core.power.CommunicationParam;
import com.jeancoder.core.power.CommunicationPowerConfig;
import com.jeancoder.core.power.CommunicationPowerHandler;
import com.jeancoder.core.power.CommunicationWorkMode;
import com.jeancoder.core.rendering.Rendering;
import com.jeancoder.core.resource.proc.Application;
import com.jeancoder.core.resource.runtime.ApplicationHolder;
import com.jeancoder.core.result.Result;
import com.jeancoder.core.util.HttpUtil;
import com.jeancoder.core.util.StringUtil;

public class DevGatewayServlet extends GatewayServlet {
	private static final long serialVersionUID = 7574607561612162094L;

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try{
			String servletPath = req.getPathInfo().replaceFirst("/", "");
			String[] resource = servletPath.split("/");
//			
//			if (resource.length == 1 && "".equals(resource[0])) {
//				//访问实例默认首页
//				req.getRequestDispatcher(SysConfigUtil.getDefaultIndexPath()).forward(req, res);
//				return;
//			}
			if (resource.length == 1 && !"".equals(resource[0])) {
				//访问app首页
				Application application = ApplicationHolder.getInstance().getAppByCode(resource[0]);
				if (application == null) {
					res.setContentType("text/html;charset=UTF-8");
					res.setStatus(HttpServletResponse.SC_NOT_FOUND);
					return;
				}
				if (StringUtil.isEmpty(application.getApp().getIndex())) {
					res.setContentType("text/html;charset=UTF-8");
					res.setStatus(HttpServletResponse.SC_NOT_FOUND);
					return;
				}
				req.getRequestDispatcher("/"+ resource[0]+ "/"+application.getApp().getIndex()).forward(req, res);
				return;
			}
			
			if (resource.length < 2) {
				res.getWriter().println(String.valueOf("请求资源路径错误"));
				return;
			}
			

			String appCode = resource[0];
			StringBuffer resourceId = new StringBuffer();
			for (int i = 1; i < resource.length; i++) {
				if (resourceId.length() != 0) {
					resourceId.append("/");
				}
				resourceId.append(resource[i]);
			}
			
			JCRequest request = new JCRequest(req);
			JCResponse response = new JCResponse(res);
			JCThreadLocal.setCode(appCode);
			JCThreadLocal.setRequest(request);
			JCThreadLocal.setResponse(response);
			// 如果是内部资源 则直接执行脚本返回json格式字符串
			if (Common.INTERNAL.equals(resource[1])) {
				internal(req, res, resource);
				return;
			}
			Application application = ApplicationHolder.getInstance().getAppByCode(appCode);
			if (application == null) {
				res.setContentType("text/html;charset=UTF-8");
				res.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			
			JCInterceptorChain chain = JCInterceptorStack.getJCInterceptorChain(appCode, resourceId.toString());
			JCThreadLocal.setInterceptorChain(chain);
			// 进入拦截器
			chain.doInterceptor(chain, application, resourceId.toString(), req, res);
			Result result =  Runner.getResult(application, req, res);
			if (result == null) {
				res.setContentType("text/html;charset=UTF-8");
				res.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return ;
			}
			Rendering rendering = RenderingFactory.getRendering(result);
			if (rendering != null) {
				rendering.process(req, res, result);
			}
		}catch(Exception e) {
			res.setContentType("text/html;charset=UTF-8");
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			e.printStackTrace();
		}
	}
	
	/**
	 *  网络请求 访问内部资源
	 */
	private void internal (HttpServletRequest req, HttpServletResponse res, String[] resource) {
		try {
			if (resource.length < 3) {
				res.setContentType("text/html;charset=UTF-8");
				res.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return ;
			}
			StringBuffer  path = new StringBuffer();
			for (int i = 2; i < resource.length; i++) {
				if (path.length() != 0) {
					path.append("/");
				}
				path.append(resource[i]);
			}
			
			List<CommunicationParam> params = new ArrayList<CommunicationParam>();
			Map<String, String[]> parameter = req.getParameterMap();
			Iterator<Entry<String, String[]>> it = parameter.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String[]> entry =  it.next();
				String key = (String) entry.getKey();
				String[] values = (String[]) entry.getValue();
				String value = null;
				if (values == null || values.length == 0) {
					value = null;
				}
				value = values[0];
				params.add(new CommunicationParam(key,value));
			}
			CommunicationPowerConfig config = new CommunicationPowerConfig();
			config.setMode(CommunicationWorkMode.NATIVE);
			CommunicationPowerHandler cph = new CommunicationPowerHandler();
			cph.init(config);
			cph.setId(resource[0]);
			String result = cph.doworkAsString(path.toString(), params);
			res.setCharacterEncoding("UTF-8");
			PrintWriter printWriter;
			printWriter = res.getWriter();
			res.setContentType(HttpUtil.JSON);
			printWriter.println(result);
		} catch (Exception e) {
			res.setContentType("text/html;charset=UTF-8");
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			e.printStackTrace();
		}
	}
 
// 
//		if (resource.length == 1 && "".equals(resource[0])) {
//			oServletPath = "crm/test";
//		}
//		if (resource.length == 1 && !"".equals(resource[0])) {
//			Application application = ApplicationHolder.getInstance().getAppByCode(resource[0]);
//			if (application == null) {
//				oServletPath = servletPath;
//			} else {
//				oServletPath += "/" + application.getApp().getIndex();
//			}
//		}
//	 
}