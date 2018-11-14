package com.jeancoder.root.server.inet;

public interface JCServer {

	ServerCode defServerCode();
	
	void start();
	
	void shutdown();
}
