package com.jeancoder.app.sdk.rendering;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jeancoder.core.rendering.Rendering;
import com.jeancoder.core.result.Result;
import com.jeancoder.root.io.http.JCHttpResponse;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpResponseStatus;

public class StreamRendering<T extends Result> extends DefaultRendering<T> implements Rendering {

	public StreamRendering(ChannelHandlerContext context) {
		super(context);
	}

	@Override
	public Object process(HttpServletRequest request, HttpServletResponse response) {
		super.process(request, response);
		Result result = this.runningResult.getResult();
		try {
			FileInputStream fis = new FileInputStream(new File(result.getResult()));
			OutputStream os = response.getOutputStream();
			int len;
			byte[] _byte = new byte[1024];
			while ((len = fis.read(_byte)) > 0) {
				os.write(_byte, 0, len);
			}
			os.flush();
			fis.close();
			this.writeResponse(HttpResponseStatus.OK, (JCHttpResponse) response, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
