package com.jeancoder.core.http;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class ChannelWrapper {

	Channel channel;
	
	public ChannelWrapper(Channel channel) {
		this.channel = channel;
	}
	
	public String id() {
		return channel.id().asLongText();
	}
	
	@Deprecated
	public void push(Object obj) {
		//channel.writeAndFlush(obj);
		throw new RuntimeException("unsupported method.");
	}
	
	public boolean pushMsg(String message) {
		if(channel==null) {
			return false;
		}
		if(!channel.isWritable()) {
			return false;
		}
		channel.writeAndFlush(new TextWebSocketFrame(message));
		return true;
	}
	
	public boolean close() {
		if(channel!=null) {
			channel.close();
		}
		return true;
	}
	
	public boolean isOpen() {
		return channel==null ? false : channel.isOpen();
	}
	
	public boolean isWritable() {
		return channel==null ? false : channel.isWritable();
	}
	
	public boolean isActive() {
		return channel==null ? false : channel.isActive();
	}
	
}
