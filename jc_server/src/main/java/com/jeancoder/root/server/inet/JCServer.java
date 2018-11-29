package com.jeancoder.root.server.inet;

import com.jeancoder.root.server.proto.conf.ServerMod;

public interface JCServer {
	
	ServerCode defServerCode();
	
	void start();
	
	void shutdown();
	
	String serverId();
	
	ServerMod info();
}
