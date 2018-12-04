package com.jeancoder.app.sdk;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jc.exception.JCException;
import com.jc.task.Runner;
import com.jc.task.TimerTaskObject;
import com.jeancoder.app.thread.JCTaskBack;
import com.jeancoder.app.thread.JCTaskCall;
import com.jeancoder.app.thread.JCTaskDelegate;
import com.jeancoder.core.cl.AppLoader;
import com.jeancoder.root.container.ContainerContextEnv;
import com.jeancoder.root.container.JCAppContainer;

import groovy.lang.Closure;

public class JCThreadMethod 
	//extends Thread 
	implements JCMethod {
	
	protected static Logger logger = LoggerFactory.getLogger(JCThreadMethod.class.getName());
	
	ScheduledExecutorService scheduExec = Executors.newScheduledThreadPool(10);
	
	public JCThreadMethod() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if(!(loader instanceof AppLoader)) {
			throw new JCException();	//detect env
		}
		//context = (AppLoader)loader;
	}
	
	public <I, O> void run(Long timeout, Closure<I> func, Closure<?> back) {
		
		JCTaskCall<I> input = new JCTaskCall<>(func);
		JCTaskBack callback = new JCTaskBack(back);
		JCTaskDelegate<I> r = new JCTaskDelegate<I>();
		r.setTimeout(timeout);
		r.setCallin(input);
		r.setCallback(callback);
		
		scheduExec.schedule(r, 0, TimeUnit.MILLISECONDS);
	}
	
	/*
	Timer timer = new Timer();
	timer.scheduleAtFixedRate(new TimerTask() {
		def task_handler = this;
		public void run() {
			KeepCertain.fuckoff(task_handler);
		}
	}, 10000, 29000);
	*/
	
	public <I> void timeTask(Long delay, Long internal, Closure<I> task) {
		AppLoader context = (AppLoader)Thread.currentThread().getContextClassLoader();
		JCAppContainer container = context.getContextEnv();
		JCAppContainer real_container = ContainerContextEnv.getCurrentContainer();
		if(real_container!=null&&real_container!=container) {
			container = real_container;
		}
		container.addTask(new TimerTaskObject(delay, internal, new Runner() {
			@Override
			public void bind() {
				this.init(task);
			}
		}));
	}
	

}
