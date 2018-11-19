package com.jeancoder.root.container;

import com.jeancoder.root.io.http.JCHttpRequest;

public interface JCAppContainer extends Lifecycle {
	
	ClassLoader getManager();
	
	String id();
	
	Object execute(JCHttpRequest req);
}
