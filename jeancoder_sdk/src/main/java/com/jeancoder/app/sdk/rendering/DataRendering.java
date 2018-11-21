package com.jeancoder.app.sdk.rendering;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jeancoder.core.rendering.Rendering;
import com.jeancoder.core.result.Result;
import com.jeancoder.core.util.JackSonBeanMapper;

import io.netty.channel.ChannelHandlerContext;

public class DataRendering extends DefaultRendering implements Rendering{

	public DataRendering(ChannelHandlerContext context) {
		super(context);
	}

	@Override
	public Object process(HttpServletRequest request, HttpServletResponse response) {
		super.process(request, response);
		Result result = this.runningResult.getResult();
		this.writeJsonResponse(JackSonBeanMapper.toJson(result.getData()), true);
		return null;
	}

}
