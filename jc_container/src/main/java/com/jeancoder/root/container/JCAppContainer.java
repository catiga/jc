package com.jeancoder.root.container;

public interface JCAppContainer extends Lifecycle {

	void onLoad();
	
	ClassLoader getManager();
}
