package com.jeancoder.root.server.inet;

import com.jc.proto.conf.ServerMod;
import com.jc.shell.ShellServer;

public interface JCServer extends ShellServer {
	
	ServerCode defServerCode();
	
	void start();
	
	void shutdown();
	
	ServerMod info();
	
}
