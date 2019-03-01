package com.jeancoder.root.io.socketx;

import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

@SuppressWarnings("serial")
public class BinaryData<T extends BinaryWebSocketFrame> extends DataBufHolder<BinaryWebSocketFrame> {

	public BinaryData(BinaryWebSocketFrame frame) {
		super(frame);
	}

	@Override
	public byte[] content() {
		return this.frame.content().array();
	}

}
