package com.jeancoder.app.sdk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jc.channel.SlaveCli;
import com.jeancoder.app.sdk.source.RequestSource;
import com.jeancoder.core.http.ChannelWrapper;
import com.jeancoder.core.http.JCRequest;

public class JCRequestMethod implements JCMethod {
	
	private static Logger logger = LoggerFactory.getLogger(JCRequestMethod.class.getName());

	public static JCRequest get() {
		return RequestSource.getRequest();
	}
	
	public static String param(String name) {
		return get().getParameter(name);
	}
	
	public static Object getAttribute(String name) {
		return get().getAttribute(name);
	}
	
	public static Object attr(String name) {
		return get().getAttribute(name);
	}
	
	public static Object read() {
		try {
			return get().wsdata().content();
		} catch(Exception e) {
			return null;
		}
	}
	
	public static ChannelWrapper channel() {
		try {
			return get().wschannel();
		} catch(Exception e) {
			e.printStackTrace();
			logger.error("", e);
			return null;
		}
	}
}
