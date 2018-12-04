package com.jeancoder.root.container.core;

import java.util.LinkedList;
import java.util.Queue;

import com.jc.task.TaskObject;
import com.jeancoder.root.container.JCAppContainer;

public abstract class QContainer extends LifecycleZa implements JCAppContainer {

	volatile private Queue<TaskObject> queue = new LinkedList<TaskObject>();

	@Override
	public boolean addTask(TaskObject obj) {
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
					task.cancel();
				}
			}
		}
	}
}
