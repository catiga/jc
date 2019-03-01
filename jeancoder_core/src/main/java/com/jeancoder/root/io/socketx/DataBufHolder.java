package com.jeancoder.root.io.socketx;

import java.io.Serializable;

import io.netty.handler.codec.http.websocketx.WebSocketFrame;

@SuppressWarnings("serial")
public abstract class DataBufHolder<T extends WebSocketFrame> implements DataBuf, Serializable {

	T frame;

	public DataBufHolder(T frame) {
		super();
		this.frame = frame;
	}
	
}
