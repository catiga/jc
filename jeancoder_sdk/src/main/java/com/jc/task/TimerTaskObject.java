package com.jc.task;

import java.util.Timer;
import java.util.TimerTask;

public class TimerTaskObject extends ConcurrentTaskObject implements TaskObject {
	
	private Timer timer;
	
	private TimerTask timerTask;
	
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
				timerTask = new TimerTask() {
					@Override
					public void run() {
						task.execute();
					}
				};
				timer.scheduleAtFixedRate(timerTask, delay, internal);
				scheduled = true;
			}
		}
	}

	@Override
	public boolean cancel() {
		synchronized(__LOCK__) {
			if(timerTask!=null&&scheduled) {
				boolean taskcelres = timerTask.cancel();
				return taskcelres;
			}
			return false;
		}
	}

	@Override
	public void shut() {
		if(timer!=null&&scheduled) {
			this.timer.cancel();
		}
	}
	
}
