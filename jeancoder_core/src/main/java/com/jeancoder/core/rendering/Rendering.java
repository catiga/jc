package com.jeancoder.core.rendering;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Rendering {
	public Object process(HttpServletRequest request, HttpServletResponse response);
}
