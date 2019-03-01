package com.jeancoder.root.io.socketx;

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

@SuppressWarnings("serial")
public class StringData<T extends TextWebSocketFrame> extends DataBufHolder<TextWebSocketFrame> {

	public StringData(T frame) {
		super(frame);
	}

	@Override
	public String content() {
		return this.frame.text();
	}

}
