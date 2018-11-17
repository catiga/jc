package com.jeancoder.app.thread;

import java.util.concurrent.Callable;

public class JCAsyncTask<I> implements Callable<I> {

	private JCTaskCall<I> func;
	
	Long timeout;
	
	boolean running = false;
	
	public JCAsyncTask(JCTaskCall<I> inseg) {
		this.func = inseg;
	}
	
	@Override
	public I call() throws Exception {
		I  ret = func.call();
		return ret;
	}

}
