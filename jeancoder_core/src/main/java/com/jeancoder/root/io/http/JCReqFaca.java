package com.jeancoder.root.io.http;

public interface JCReqFaca {

	static final String FORWARD_REQUEST_CONTEXT = JCReqFaca.class.getName() + ".CONTEXT";
	
	static final String FORWARD_REQUEST_PATH = JCReqFaca.class.getName() + ".PATH";
	
	static final String FORWARD_REQUEST_QUERY = JCReqFaca.class.getName() + ".QUERY";
	
	static final String FORWARD_REQUEST_FULLURI = JCReqFaca.class.getName() + ".FULLURI";
}
