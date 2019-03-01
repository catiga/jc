package com.jeancoder.core.http;

import java.io.Serializable;

import io.netty.channel.Channel;

@SuppressWarnings("serial")
public class ChannelWrapper implements Serializable{

	Channel channel;
	
	public ChannelWrapper(Channel channel) {
		this.channel = channel;
	}
	
	public String id() {
		return channel.id().asLongText();
	}
	
	public void push(Object obj) {
		channel.writeAndFlush(obj);
	}
	
}
