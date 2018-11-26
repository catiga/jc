package com.jeancoder.core.rendering;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeancoder.core.result.Result;
import com.jeancoder.core.util.HttpUtil;
import com.jeancoder.root.env.JCAPP;
import com.jeancoder.root.io.http.JCHttpResponse;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpResponseStatus;

public class StaticRendering<T extends Result> extends DefaultRendering<T> implements Rendering {

	private static Logger logger = LoggerFactory.getLogger(StaticRendering.class);
	
	public StaticRendering(ChannelHandlerContext context) {
		super(context);
	}

	@Override
	public Object process(HttpServletRequest request, HttpServletResponse response) {
		super.process(request, response);
		Result result = this.runningResult.getResult();
		
		JCAPP apps = this.runningResult.getAppins();
		String path = apps.getApp_base() + "/" + apps.getSta_base() + "/";
		String name = result.getResult();
		name = path + name;
		logger.info("static_name=" + name);
		try {
			response.setContentType(HttpUtil.getContentType(result.getResult()));
			BufferedInputStream fis = new BufferedInputStream(new FileInputStream(new File(name)));
			OutputStream os = response.getOutputStream();
			int len;
			byte[] _byte = new byte[128];
			while ((len = fis.read(_byte)) > 0) {
				os.write(_byte, 0, len);
			}
			os.flush();
			fis.close();
			this.writeResponse(HttpResponseStatus.OK, (JCHttpResponse) response, true);
		} catch (Exception e) {
			logger.error("name not found", e);
		}
		return null;
	}

}
