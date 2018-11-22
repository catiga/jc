package com.jeancoder.core.rendering;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jeancoder.core.rendering.Rendering;
import com.jeancoder.core.result.Result;
import com.jeancoder.root.io.http.JCHttpResponse;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpResponseStatus;

public class RedirectRendering<T extends Result> extends DefaultRendering<T> implements Rendering {

	public RedirectRendering(ChannelHandlerContext context) {
		super(context);
	}

	@Override
	public Object process(HttpServletRequest request, HttpServletResponse response) {
		super.process(request, response);
		Result result = this.runningResult.getResult();
		try {
			((JCHttpResponse)response).sendRedirect(result.getResult());
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.writeResponse(HttpResponseStatus.OK, (JCHttpResponse)response, true);
		return null;
	}

}