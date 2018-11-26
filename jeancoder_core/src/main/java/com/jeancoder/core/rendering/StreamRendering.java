package com.jeancoder.core.rendering;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeancoder.core.result.Result;
import com.jeancoder.root.io.http.ContentTypes;

import io.netty.channel.ChannelHandlerContext;

public class StreamRendering<T extends Result> extends DefaultRendering<T> implements Rendering {

	private static Logger logger = LoggerFactory.getLogger(StreamRendering.class);
	
	public StreamRendering(ChannelHandlerContext context) {
		super(context);
	}

	@Override
	public Object process(HttpServletRequest request, HttpServletResponse response) {
		super.process(request, response);
		Result result = this.runningResult.getResult();
		String name = result.getResult();
		String content_key = name.substring(name.lastIndexOf("."));
		String content_type = ContentTypes.get(content_key);
		try {
			//response.setContentType(HttpUtil.getContentType(result.getResult()));
			BufferedInputStream fis = new BufferedInputStream(new FileInputStream(new File(name)));
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);  
            byte[] b = new byte[1000];  
            int n;  
            while ((n = fis.read(b)) != -1) {  
                bos.write(b, 0, n);  
            }  
            fis.close();  
            bos.close();  
            byte[] buffer = bos.toByteArray();
            
			this.writeStreamResponse(buffer, content_type, true);
		} catch (Exception e) {
			logger.error(name + " render error:", e);
		}
		return null;
	}

}
