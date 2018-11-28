package com.jeancoder.root.server.channels;

import io.netty.channel.ChannelHandler;

public interface ChannelHandlerHolder {  
	  
    ChannelHandler[] handlers();  
} 
