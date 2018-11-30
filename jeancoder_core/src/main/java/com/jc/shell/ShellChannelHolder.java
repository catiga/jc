package com.jc.shell;

import java.util.Enumeration;
import java.util.List;

import io.netty.channel.Channel;

public interface ShellChannelHolder {

	
	public List<String> dispatchlist();
	
	public Channel dispatchaim(String id);
	
	public <T extends ShellServer> Enumeration<T> servers();
	
}
