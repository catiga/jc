package com.jeancoder.app.sdk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeancoder.app.sdk.Interceptor.JCInterceptor;
import com.jeancoder.root.container.ContainerContextEnv;
import com.jeancoder.root.container.JCAppContainer;

public class JC {

	protected static Logger logger = LoggerFactory.getLogger(JC.class);
	
	public static class interceptor {
		public static void add(String __pre__, String __post__) {
			JCInterceptor interceptor = new JCInterceptor();
			interceptor.setPreResource(__pre__);
			interceptor.setPostResource(__post__);
			JCAppContainer container = ContainerContextEnv.getCurrentContainer();
			container.addInterceptor(interceptor);
		}
	}
	
	public static final JCRequestMethod request = new JCRequestMethod();
	
	public static final JCInternalMethod internal = new JCInternalMethod();
	
	public static final JCExtractMethod extract = new JCExtractMethod();
	
	public static final JCRemoteMethod remote = new JCRemoteMethod();
	
	public static final JCThreadMethod thread = new JCThreadMethod();
	
}
