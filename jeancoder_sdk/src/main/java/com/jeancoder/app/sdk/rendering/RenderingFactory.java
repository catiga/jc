package com.jeancoder.app.sdk.rendering;

import com.jeancoder.core.rendering.Rendering;
import com.jeancoder.core.result.Result;
import com.jeancoder.core.result.ResultType;
import com.jeancoder.root.env.RunnerResult;

import io.netty.channel.ChannelHandlerContext;

public class RenderingFactory {

	public static Rendering getRendering(ChannelHandlerContext context, RunnerResult<Result> resultWrapper) {
		DefaultRendering rend = new DefaultRendering(context);
		Result result = resultWrapper.getResult();
		if(result!=null) {
			if (ResultType.VIEW_RESOURCE.equals(result.getResultType())) {
				rend = new WelcomeApplicationRendering(context);
			} else if (ResultType.DATA_RESOURCE.equals(result.getResultType())) {
				rend = new DataRendering(context);
			} else if (ResultType.STATIC_RESOURCE.equals(result.getResultType())) {
				rend = new StaticRendering(context);
			} else if (ResultType.REDIRECT_CONTROLLER_RESOURCE.equals(result.getResultType())) {
				rend = new RedirectRendering(context);
			} else if (ResultType.CONTROLLER_RESOURCE.equals(result.getResultType())) {
				rend = new ForwardingRendering(context);
			}
		}
		rend.runningResult = resultWrapper;
		return rend;
	}
}
