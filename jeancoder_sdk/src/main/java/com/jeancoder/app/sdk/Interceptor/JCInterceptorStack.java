package com.jeancoder.app.sdk.Interceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jeancoder.core.Interceptor.Interceptor;
import com.jeancoder.core.Interceptor.InterceptorStack;
import com.jeancoder.core.common.Common;
import com.jeancoder.core.exception.SdkRuntimeException;
import com.jeancoder.core.namer.NamerApplication;

public class JCInterceptorStack implements InterceptorStack{
	private static Map<String, List<Interceptor>> interceptorMap = new HashMap<String, List<Interceptor>>();
	private static List<Interceptor> sysInterceptorList = new ArrayList<Interceptor>();
	private static JCInterceptorStack jcInterceptorStackSingleton = new JCInterceptorStack();
	
	private JCInterceptorStack(){
		
	}
	/**
	 * 系统变量加载拦截器时标志哪个APP加载的拦截器
	 */
	private static NamerApplication nameApplication = null;

	@Override
	public void addInterceptor(Interceptor interceptor) {
		if (nameApplication == null && nameApplication.getAppCode() == null || nameApplication.getAppCode().equals("")) {
			throw new SdkRuntimeException("appCode is null, add Interceptor failure.");
		}
		List<Interceptor> interceptorList = interceptorMap.get(nameApplication.getAppCode());
		if (interceptorList == null) {
			interceptorList = new ArrayList<Interceptor>();
		}
		interceptorList.add(interceptor);
		interceptorMap.put(nameApplication.getAppCode(), interceptorList);
	}

//	@Override
//	public void addSysInterceptor(Interceptor interceptor) {
//		if (nameApplication == null && nameApplication.getAppCode() == null || nameApplication.getAppCode().equals("")) {
//			throw new SdkRuntimeException("appCode is null, add Interceptor failure.");
//		}
//		JCSystInterceptor jcSystInterceptor = new JCSystInterceptor();
//		jcSystInterceptor.setPostResource(interceptor.getPostResource());
//		jcSystInterceptor.setPreResource(interceptor.getPreResource());
//		jcSystInterceptor.setAppCode(nameApplication.getAppCode());
//		sysInterceptorList.add(jcSystInterceptor);
//	}
//	
	
	public static void setNamerApplication(NamerApplication nameApplicationValue){
		nameApplication = nameApplicationValue;
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
		return new JCInterceptorChain(new ArrayList<Interceptor>(sysInterceptorList), appInterceptorList);
	}
	
	/**
	 * 根据Appcode 返回拦截器链
	 * @return
	 */
	public static void removeJCInterceptor(String appCode){
		interceptorMap.remove(appCode);
		List<Interceptor> list = new ArrayList<Interceptor>();
		for (Interceptor i: sysInterceptorList) {
			JCSystInterceptor sysInterceptor = (JCSystInterceptor)i;
			if (appCode.equals(sysInterceptor.getAppCode())) {
				continue;
			}
			list.add(i);
		}
		sysInterceptorList = list;
	}
}