package com.jeancoder.root.env;

import io.netty.channel.ChannelHandlerContext;

public class ChannelContextWrapper {

	ChannelHandlerContext context;

	public ChannelHandlerContext getContext() {
		return context;
	}
	
	private ChannelContextWrapper(ChannelHandlerContext context) {
		this.context = context;
	}
	
	public static ChannelContextWrapper newone(ChannelHandlerContext context) {
		return new ChannelContextWrapper(context);
	}
}
