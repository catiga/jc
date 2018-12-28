package com.jeancoder.root.container;

import com.jc.task.TaskObject;

public interface JCTaskKeeper {

	boolean addTask(TaskObject obj);
	
	//should not have this method called privilege
	//void offTask();
}
