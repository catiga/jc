package com.jeancoder.root.io.socketx;

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

@SuppressWarnings("serial")
public class StringData<T extends TextWebSocketFrame> extends DataBufHolder<TextWebSocketFrame> {

	public StringData(T frame) {
		super(frame);
	}

	@Override
	public String content() {
		String textValue = this.frame.text();
		if(textValue.equals("null")) {
			textValue = null;
		}
		return textValue;
	}

}
