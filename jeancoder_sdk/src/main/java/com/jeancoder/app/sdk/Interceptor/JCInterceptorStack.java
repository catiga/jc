package com.jeancoder.app.sdk.Interceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jeancoder.core.Interceptor.Interceptor;
import com.jeancoder.core.Interceptor.InterceptorStack;
import com.jeancoder.core.common.Common;
import com.jeancoder.root.container.ContainerContextEnv;
import com.jeancoder.root.container.JCAppContainer;

public class JCInterceptorStack implements InterceptorStack{
	private static Map<String, List<Interceptor>> interceptorMap = new HashMap<String, List<Interceptor>>();
	private static JCInterceptorStack jcInterceptorStackSingleton = new JCInterceptorStack();
	
	private JCInterceptorStack(){
		
	}
	/**
	 * 系统变量加载拦截器时标志哪个APP加载的拦截器
	 */
	//private static NamerApplication nameApplication = null;

	@Override
	public void addInterceptor(Interceptor interceptor) {
		JCAppContainer container = ContainerContextEnv.getCurrentContainer();
//			JCAPP ins = container.getApp();
//			List<Interceptor> interceptorList = interceptorMap.get(ins.getCode());
//			if (interceptorList == null) {
//				interceptorList = new ArrayList<Interceptor>();
//			}
//			String pre_clz = null;
//			if(interceptor.getPreResource()!=null) {
//				pre_clz = ins.getOrg() + "." + ins.getDever() + "." + ins.getCode() + "." + Common.INTERCEPTOR + "." + interceptor.getPreResource().replace("/", ".");
//			}
//			String pos_clz = null;
//			if(interceptor.getPostResource()!=null) {
//				pos_clz = ins.getOrg() + "." + ins.getDever() + "." + ins.getCode() + "." + Common.INTERCEPTOR + "." + interceptor.getPostResource().replace("/", ".");
//			}
//			interceptor.setPostResource(pos_clz);
//			interceptor.setPreResource(pre_clz);
//			interceptorList.add(interceptor);
//			interceptorMap.put(ins.getCode(), interceptorList);
			
		container.addInterceptor(interceptor);
	}

	public static InterceptorStack getInterceptorStack(){
		return jcInterceptorStackSingleton;
	}
	
	/**
	 * 根据Appcode 返回拦截器链
	 * @return
	 */
	public static JCInterceptorChain getJCInterceptorChain(String appCode, String resourceId){
		if (resourceId.indexOf(Common.STATIC + "/") == 0 || resourceId.indexOf(Common.VIEW + "/") == 0){
			return new JCInterceptorChain();
		}
		List<Interceptor> appInterceptorList = null;
		if (interceptorMap.get(appCode) == null) {
			appInterceptorList = new ArrayList<Interceptor>();
		} else {
			appInterceptorList =  new ArrayList<Interceptor>(interceptorMap.get(appCode));
		}
		return new JCInterceptorChain(appInterceptorList);
	}
	
	/**
	 * 根据Appcode 返回拦截器链
	 * @return
	 */
	public static void removeJCInterceptor(String appCode){
		interceptorMap.remove(appCode);
	}
}

