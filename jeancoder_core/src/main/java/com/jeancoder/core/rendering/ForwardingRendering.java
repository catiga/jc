package com.jeancoder.core.rendering;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jeancoder.core.rendering.Rendering;
import com.jeancoder.core.result.Result;
import com.jeancoder.root.io.http.JCHttpResponse;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpResponseStatus;

public class ForwardingRendering<T extends Result> extends DefaultRendering<T> implements Rendering {

	public ForwardingRendering(ChannelHandlerContext context) {
		super(context);
	}

	@Override
	public Object process(HttpServletRequest request, HttpServletResponse response) {
		super.process(request, response);
		//Result result = this.runningResult;
		this.writeResponse(HttpResponseStatus.OK, (JCHttpResponse)response, true);
		return null;
	}

}
