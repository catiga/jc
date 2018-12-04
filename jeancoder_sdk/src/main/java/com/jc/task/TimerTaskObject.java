package com.jc.task;

import java.util.Timer;
import java.util.TimerTask;

public class TimerTaskObject extends ConcurrentTaskObject implements TaskObject {

	private Timer timer;
	
	Long delay = 0L;
	
	Long internal = 0L;
	
	Runner task;
	
	boolean scheduled = false;
	
	public TimerTaskObject(Long delay, Long internal, Runner task) {
		timer = new Timer();
		this.delay = delay;
		this.internal = internal;
		this.task = task;
		this.scheduled = false;
	}

	@Override
	public void run() {
		synchronized (__LOCK__) {
			if(!scheduled) {
				timer.scheduleAtFixedRate(new TimerTask() {
					public void run() {
						task.execute();
					}
				}, delay, internal);
			}
		}
	}

	@Override
	public void cancel() {
		synchronized(__LOCK__) {
			if(timer!=null&&scheduled) {
				timer.cancel();
			}
		}
	}
	
}
