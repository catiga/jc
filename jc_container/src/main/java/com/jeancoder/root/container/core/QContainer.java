package com.jeancoder.root.container.core;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jc.task.TaskObject;
import com.jeancoder.root.container.JCAppContainer;

@SuppressWarnings("serial")
public abstract class QContainer extends LifecycleZa implements JCAppContainer {
	
	private static Logger logger = LoggerFactory.getLogger(QContainer.class.getName());

	volatile private List<TaskObject> queue = new LinkedList<TaskObject>();

	@Override
	public boolean addTask(TaskObject obj) {
		logger.info(this + " add new task:::" + obj);
		synchronized(queue) {
			obj.run();
			//boolean opresult = queue.offer(obj);
			boolean opresult = queue.add(obj);
			logger.info(this.getApp().getCode() + " total task size===" + queue.size());
			return opresult;
		}
	}
	
	public void offTask() {
		if(!queue.isEmpty()) {
			synchronized (queue) {
				Iterator<TaskObject> its = queue.iterator();
				while(its.hasNext()) {
					//TaskObject task = queue.poll();
					TaskObject task = its.next();
					if(task!=null) {
						boolean cancel_result = task.cancel();
						task.shut();
						logger.info(this + " " + task + " cancel task:::" + cancel_result);
						its.remove();
					}
				}
			}
		}
	}
	
	public String toString() {
		return "QContainere" + ":::" + this.getApp().getCode();
	}
}
