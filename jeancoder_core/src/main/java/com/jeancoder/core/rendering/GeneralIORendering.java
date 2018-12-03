package com.jeancoder.core.rendering;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeancoder.core.result.Result;
import com.jeancoder.root.io.JcServletOutputStream;
import com.jeancoder.root.io.http.ContentTypes;

import io.netty.channel.ChannelHandlerContext;

public class GeneralIORendering<T extends Result> extends DefaultRendering<T> implements Rendering {

	private static Logger logger = LoggerFactory.getLogger(GeneralIORendering.class);
	
	public GeneralIORendering(ChannelHandlerContext context) {
		super(context);
	}

	@Override
	public Object process(HttpServletRequest request, HttpServletResponse response) {
		super.process(request, response);
		Result result = this.runningResult.getResult();
		
		String content_type = response.getContentType();
		//JCAPP apps = this.runningResult.getAppins();
		String name = result.getResult();	//general path;
		String content_key = "";
		if(content_type==null&&name!=null&&name.lastIndexOf(".")>-1) {
			content_key = name.substring(name.lastIndexOf("."));
			content_type = ContentTypes.get(content_key);
		}
		if(name!=null&&name.lastIndexOf("/")>-1) {
			name = name.substring(name.lastIndexOf("/") + 1);
		}
		try {
			JcServletOutputStream jcos = (JcServletOutputStream)response.getOutputStream();
            byte[] buffer = jcos.getData();
            
			this.writeStreamResponse(buffer, content_type, name, true);
		} catch (Exception e) {
			logger.error(name + " rendering error:", e);
		}
		return null;
	}

}
