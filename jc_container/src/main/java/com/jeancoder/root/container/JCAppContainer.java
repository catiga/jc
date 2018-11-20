package com.jeancoder.root.container;

import com.jeancoder.root.container.core.BCID;
import com.jeancoder.root.io.http.JCHttpRequest;

public interface JCAppContainer extends Lifecycle {
	
	ClassLoader getManager();
	
	public BCID id();
	
	<T> T execute(JCHttpRequest req);
}
