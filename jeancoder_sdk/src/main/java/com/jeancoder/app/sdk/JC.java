package com.jeancoder.app.sdk;

import com.jeancoder.app.sdk.Interceptor.JCInterceptor;
import com.jeancoder.app.sdk.source.InterceptorStackSource;
import com.jeancoder.core.Interceptor.InterceptorStack;

public class JC {

	public static class interceptor {
		public static void add(String __pre__, String __post__) {
			InterceptorStack is = InterceptorStackSource.getInterceptorStack();
			JCInterceptor interceptor = new JCInterceptor();
			interceptor.setPreResource(__pre__);
			interceptor.setPostResource(__post__);
			is.addInterceptor(interceptor);
		}
	}
	
	public static final JCRequestMethod request = new JCRequestMethod();
	
	public static final JCInternalMethod internal = new JCInternalMethod();
	
	public static final JCExtractMethod extract = new JCExtractMethod();
	
	public static final JCRemoteMethod remote = new JCRemoteMethod();
	
	public static final JCThreadMethod thread = new JCThreadMethod();
	
}
