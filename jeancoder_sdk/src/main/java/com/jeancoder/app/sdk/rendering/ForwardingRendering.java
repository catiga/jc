package com.jeancoder.app.sdk.rendering;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jeancoder.core.http.JCRequest;
import com.jeancoder.core.rendering.Rendering;
import com.jeancoder.core.result.Result;

public class ForwardingRendering implements Rendering {

	@Override
	public void process(HttpServletRequest request, HttpServletResponse response, Result result) throws Exception {
		request.getRequestDispatcher(new JCRequest(request).getContextPath() + "/" + result.getResult()).forward(request, response);
	}

}
