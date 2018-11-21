package com.jeancoder.root.container;

import com.jeancoder.core.result.Result;
import com.jeancoder.root.container.core.BCID;
import com.jeancoder.root.env.Lifecycle;
import com.jeancoder.root.env.RunnerResult;
import com.jeancoder.root.io.http.JCHttpRequest;
import com.jeancoder.root.io.http.JCHttpResponse;

public interface JCAppContainer extends Lifecycle {
	
	ClassLoader getManager();
	
	public BCID id();
	
	<T extends Result> RunnerResult<T> execute(JCHttpRequest req, JCHttpResponse res);
}
