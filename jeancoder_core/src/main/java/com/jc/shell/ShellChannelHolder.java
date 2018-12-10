package com.jc.shell;

import java.util.Enumeration;
import java.util.List;

import com.jc.proto.msg.GeneralMsg;

import io.netty.channel.Channel;

public interface ShellChannelHolder {

	public GeneralMsg consumeMsg(String message_id);
	
	public boolean emptyMsg(String message_id);
	
	public List<String> dispatchlist();
	
	public Channel dispatchaim(String id);
	
	public <T extends ShellServer> Enumeration<T> servers();
	
}
