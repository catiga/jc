package com.jeancoder.core.rendering;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jeancoder.core.result.Result;
import com.jeancoder.core.util.JackSonBeanMapper;

import io.netty.channel.ChannelHandlerContext;

public class DataRendering<T extends Result> extends DefaultRendering<T> implements Rendering{

	public DataRendering(ChannelHandlerContext context) {
		super(context);
	}

	@Override
	public Object process(HttpServletRequest request, HttpServletResponse response) {
		super.process(request, response);
		Result result = this.runningResult.getResult();
		if(result.getData() instanceof String) {
			Object data_obj = result.getData();
			if(data_obj!=null)
				this.writeJsonResponse(result.getData().toString(), true);
			else
				this.writeJsonResponse("{}", true);
		} else {
			this.writeJsonResponse(JackSonBeanMapper.toJson(result.getData()), true);
		}
		return null;
	}

}
