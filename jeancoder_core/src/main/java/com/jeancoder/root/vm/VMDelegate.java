package com.jeancoder.root.vm;

import com.jeancoder.root.env.ChannelContextWrapper;

public interface VMDelegate {

	public JCVM getVM();
	
	public ChannelContextWrapper getCurrentContext();
	
}
