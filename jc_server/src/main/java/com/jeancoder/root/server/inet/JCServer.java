package com.jeancoder.root.server.inet;

import com.jc.shell.ShellServer;
import com.jeancoder.root.server.proto.conf.AppMod;
import com.jeancoder.root.server.proto.conf.ServerMod;

public interface JCServer extends ShellServer {
	
	ServerCode defServerCode();
	
	void start();
	
	void shutdown();
	
	String serverId();
	
	ServerMod info();
	
	void updateApp(AppMod appMod);
}
