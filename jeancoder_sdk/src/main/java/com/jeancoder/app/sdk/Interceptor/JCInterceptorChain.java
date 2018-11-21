package com.jeancoder.app.sdk.Interceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jeancoder.app.sdk.runner.Runner;
import com.jeancoder.core.Interceptor.Interceptor;
import com.jeancoder.core.Interceptor.InterceptorChain;
import com.jeancoder.core.common.Common;
import com.jeancoder.core.exception.SdkRuntimeException;
import com.jeancoder.core.http.JCThreadLocal;
import com.jeancoder.core.resource.proc.Application;
import com.jeancoder.core.resource.proc.InterceptorGroovyDynamicResource;
import com.jeancoder.core.resource.proc.Resource;
import com.jeancoder.core.resource.runtime.ApplicationHolder;
import com.jeancoder.core.result.Result;
import com.jeancoder.core.util.StringUtil;

public class JCInterceptorChain  implements InterceptorChain {
	
	private  List<Interceptor> interceptorChain = new ArrayList<Interceptor>();
	
	public JCInterceptorChain () {
	}
	public JCInterceptorChain (List<Interceptor> interceptorChain) {
		this.interceptorChain = interceptorChain;
	}
	public void setInterceptorChain(List<Interceptor> interceptorChain) {
		this.interceptorChain = interceptorChain;
	}
	
	public List<Interceptor> getInterceptorChain() {
		return interceptorChain;
	}
	
	public Interceptor next(){
		if (interceptorChain != null && interceptorChain.size() != 0) {
			return interceptorChain.remove(0);
		}
		return null;
	}
	
	public void doInterceptor(JCInterceptorChain chain, Application application, String resourceId, HttpServletRequest request, HttpServletResponse response) throws IOException{
		Interceptor interceptor = chain.next();
		if (interceptor == null) {
			Result result = Runner.run(application, resourceId);
			if (result == null) {
				response.setContentType("text/html;charset=UTF-8");
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			} 
			JCThreadLocal.setResult(result);
			return;
		}
		
		boolean  isSuccess = true;
		if (!StringUtil.isEmpty(StringUtil.trim(interceptor.getPreResource()))) {
			Resource resourcer = getInterceptorResource(application, interceptor, interceptor.getPreResource());
			if (resourcer == null) {
				throw new SdkRuntimeException(interceptor.getPreResource() + " no found ");
			}
			String uri_now = request.getRequestURI();
			uri_now = uri_now.substring(("/" + application.getAppCode()).length());
			Object relst = ((InterceptorGroovyDynamicResource)resourcer).getResult(uri_now);
			if (relst instanceof Boolean) {
				isSuccess = (Boolean)relst;
			} else {
				isSuccess = false;
			}
		}
		if (!isSuccess) {
			return; 
		}
		
		doInterceptor(chain, application, resourceId, request, response);
		
		if (!StringUtil.isEmpty(StringUtil.trim(interceptor.getPostResource()))) {
			Resource postResource = getInterceptorResource(application, interceptor, interceptor.getPostResource());
			if (postResource == null) {
				throw new SdkRuntimeException(interceptor.getPostResource() + " no found ");
			}
			postResource.getResult();
		}
		return;
	}
	
	
	/**
	 * 全域拦截器 需要拦截器对应的application，再找拦截器脚本执行
	 * @param application
	 * @param interceptor
	 * @param interceptorResource
	 * @return
	 */
	private Resource getInterceptorResource(Application application, Interceptor interceptor, String interceptorResource) {
		if (interceptor instanceof JCSystInterceptor) {
			JCSystInterceptor sysInterceptor = (JCSystInterceptor)interceptor;
			Application sysApplication = ApplicationHolder.getInstance().getAppByCode(sysInterceptor.getAppCode());
			if (sysApplication == null) {
				return null;
			}
			return sysApplication.getResource(Common.INTERCEPTOR, interceptorResource);
		}
		return application.getResource(Common.INTERCEPTOR, interceptorResource);
	}
	
}
