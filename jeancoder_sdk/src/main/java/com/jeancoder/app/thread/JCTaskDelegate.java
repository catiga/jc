package com.jeancoder.app.thread;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.jeancoder.app.sdk.extract.JCExtract;
import com.jeancoder.app.sdk.source.LoggerSource;
import com.jeancoder.core.log.JCLogger;

public class JCTaskDelegate<I> implements Runnable {
	
	private static final JCLogger LOGGER = LoggerSource.getLogger(JCExtract.class.getName());

	Long timeout;
	
	JCTaskBack callback;
	
	JCTaskCall<I> callin;

	@Override
	public void run() {
		TaskResult result = new TaskResult();
		
		FutureTask<I> futureTask = new FutureTask<I>(new JCAsyncTask<>(callin));
		
		Executor executor=Executors.newSingleThreadExecutor();
		executor.execute(futureTask);
		
		try {
			I normal_ret = futureTask.get(timeout, TimeUnit.MILLISECONDS);
			result.data = normal_ret;
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			String code = "10001";
			String msg = "execute timeout, and setting time rans=" + timeout;
			CallbackException cex = new CallbackException(e, code, msg);
			LOGGER.error(code + ":" + msg);
			result.code = code;
			result.msg = msg;
			result.ex = cex;
			futureTask.cancel(true);
		} catch(Exception e) {
			String code = "20001";
			String msg = "exception happening";
			CallbackException cex = new CallbackException(e, code, msg);
			LOGGER.error("task error", cex);
			result.code = code;
			result.msg = msg;
			result.ex = cex;
		}
		
		callback.doAnything(result);
	}

	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}

	public void setCallback(JCTaskBack callback) {
		this.callback = callback;
	}

	public void setCallin(JCTaskCall<I> callin) {
		this.callin = callin;
	}

}
