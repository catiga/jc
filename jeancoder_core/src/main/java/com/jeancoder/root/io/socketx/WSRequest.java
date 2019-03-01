package com.jeancoder.root.io.socketx;

import java.io.IOException;

import com.jeancoder.core.http.ChannelWrapper;
import com.jeancoder.root.io.http.JCHttpRequest;

import io.netty.handler.codec.http.FullHttpRequest;

public class WSRequest<T extends DataBuf> extends JCHttpRequest {
	
	private T data;
	
	private ChannelWrapper channel;

	public WSRequest(FullHttpRequest request) throws IOException {
		super(request);
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public ChannelWrapper getChannel() {
		return channel;
	}

	public void setChannel(ChannelWrapper channel) {
		this.channel = channel;
	}
	
}
