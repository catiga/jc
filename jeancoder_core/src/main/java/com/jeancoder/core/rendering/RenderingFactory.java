package com.jeancoder.core.rendering;

import com.jeancoder.core.rendering.Rendering;
import com.jeancoder.core.result.Result;
import com.jeancoder.core.result.ResultType;
import com.jeancoder.root.env.RunnerResult;

import io.netty.channel.ChannelHandlerContext;

public class RenderingFactory {

	public static <T extends Result> Rendering getRendering(ChannelHandlerContext context, RunnerResult<T> resultWrapper) {
		DefaultRendering<T> rend = new DefaultRendering<T>(context);
		Result result = resultWrapper.getResult();
		if(result!=null) {
			if (ResultType.VIEW_RESOURCE.equals(result.getResultType())) {
				rend = new WelcomeApplicationRendering<T>(context);
			} else if (ResultType.DATA_RESOURCE.equals(result.getResultType())) {
				rend = new DataRendering<T>(context);
			} else if (ResultType.STATIC_RESOURCE.equals(result.getResultType())) {
				rend = new StaticRendering<T>(context);
			} else if (ResultType.REDIRECT_CONTROLLER_RESOURCE.equals(result.getResultType())) {
				rend = new RedirectRendering<T>(context);
			} else if (ResultType.CONTROLLER_RESOURCE.equals(result.getResultType())) {
				rend = new ForwardingRendering<T>(context);
			}
		}
		rend.runningResult = resultWrapper;
		return rend;
	}
}
