package com.jeancoder.app.sdk.rendering;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jeancoder.core.rendering.Rendering;
import com.jeancoder.core.result.Result;
import com.jeancoder.core.util.HttpUtil;
import com.jeancoder.core.util.JackSonBeanMapper;

public class DataRendering implements Rendering{

	@Override
	public void process(HttpServletRequest request, HttpServletResponse response, Result result) throws Exception {
		response.setCharacterEncoding("UTF-8");
		PrintWriter printWriter = response.getWriter();
		if (result.getData() instanceof String) {
			response.setContentType(HttpUtil.HTML);
			printWriter.println(result.getData());
		} else {
			response.setContentType(HttpUtil.JSON);
			printWriter.println(JackSonBeanMapper.toJson(result.getData()));
		}
		//printWriter.close();
	}

}
