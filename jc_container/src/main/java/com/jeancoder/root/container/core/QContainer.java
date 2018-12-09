package com.jeancoder.root.container.core;

import java.util.LinkedList;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jc.task.TaskObject;
import com.jeancoder.root.container.JCAppContainer;

@SuppressWarnings("serial")
public abstract class QContainer extends LifecycleZa implements JCAppContainer {
	
	private static Logger logger = LoggerFactory.getLogger(QContainer.class.getName());

	volatile private Queue<TaskObject> queue = new LinkedList<TaskObject>();

	@Override
	public boolean addTask(TaskObject obj) {
		logger.info(this + " add new task:::" + obj);
		synchronized(queue) {
			obj.run();
			boolean opresult = queue.offer(obj);
			return opresult;
		}
	}
	
	public void offTask() {
		if(!queue.isEmpty()) {
			synchronized (queue) {
				TaskObject task = queue.poll();
				if(task!=null) {
					logger.info(this + " cancel task:::" + task);
					task.cancel();
				}
			}
		}
	}
	
	public String toString() {
		return "QContainere" + ":::" + this.getApp().getCode();
	}
}
