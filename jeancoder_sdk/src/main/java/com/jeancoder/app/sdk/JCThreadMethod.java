package com.jeancoder.app.sdk;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.jeancoder.app.thread.JCTaskBack;
import com.jeancoder.app.thread.JCTaskCall;
import com.jeancoder.app.thread.JCTaskDelegate;

import groovy.lang.Closure;

public class JCThreadMethod extends Thread implements JCMethod {
	
	ScheduledExecutorService scheduExec = Executors.newScheduledThreadPool(50);
	
	public <I, O> void run(Long timeout, Closure<I> func, Closure<?> back) {
		
		JCTaskCall<I> input = new JCTaskCall<>(func);
		JCTaskBack callback = new JCTaskBack(back);
		JCTaskDelegate<I> r = new JCTaskDelegate<I>();
		r.setTimeout(timeout);
		r.setCallin(input);
		r.setCallback(callback);
		
		scheduExec.schedule(r, 0, TimeUnit.MILLISECONDS);
	}

}
