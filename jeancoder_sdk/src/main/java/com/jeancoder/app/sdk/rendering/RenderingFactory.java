package com.jeancoder.app.sdk.rendering;

import com.jeancoder.core.rendering.Rendering;
import com.jeancoder.core.result.Result;
import com.jeancoder.core.result.ResultType;

public class RenderingFactory {

	public static Rendering getRendering(Result result) {
		
		if (ResultType.VIEW_RESOURCE.equals(result.getResultType())) {
			return new WelcomeApplicationRendering();
		} else if (ResultType.DATA_RESOURCE.equals(result.getResultType())) {
			return new DataRendering();
		} else if (ResultType.STATIC_RESOURCE.equals(result.getResultType())) {
			return new StaticRendering();
		} else if (ResultType.REDIRECT_CONTROLLER_RESOURCE.equals(result.getResultType())) {
			return new RediectRendering();
		} else if (ResultType.CONTROLLER_RESOURCE.equals(result.getResultType())) {
			return new ForwardingRendering();
		} 
		return null;
	}
}
