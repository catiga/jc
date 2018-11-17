package com.jeancoder.core.rendering;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jeancoder.core.result.Result;

public interface Rendering {
	public void process(HttpServletRequest request, HttpServletResponse response, Result result) throws Exception;
}
