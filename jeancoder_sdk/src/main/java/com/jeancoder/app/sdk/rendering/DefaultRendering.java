package com.jeancoder.app.sdk.rendering;


import static com.jeancoder.root.io.line.HeaderNames.CONTENT_LENGTH;
import static com.jeancoder.root.io.line.HeaderNames.CONTENT_TYPE;
import static com.jeancoder.root.io.line.HeaderNames.LOCATION;
import static io.netty.buffer.Unpooled.copiedBuffer;

import java.io.File;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jeancoder.core.rendering.Rendering;
import com.jeancoder.core.result.Result;
import com.jeancoder.root.env.RunnerResult;
import com.jeancoder.root.io.http.JCHttpRequest;
import com.jeancoder.root.io.http.JCHttpResponse;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

public class DefaultRendering implements Rendering {

	protected ChannelHandlerContext context;
	
	protected RunnerResult<Result> runningResult;
	
	protected JCHttpRequest request;
	
	protected JCHttpResponse response;
	
	public ChannelHandlerContext getContext() {
		return context;
	}

	public DefaultRendering(ChannelHandlerContext context) {
		super();
		this.context = context;
	}

	@Override
	public Object process(HttpServletRequest request, HttpServletResponse response) {
		this.request = (JCHttpRequest)request;
		this.response = (JCHttpResponse)response;
		return null;
	}

	protected void setContentTypeHeader(File file) {
        MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
        response.delegateObj().headers().set(CONTENT_TYPE, mimetypesFileTypeMap.getContentType(file.getPath()));
    }
	
	protected void sendRedirect(String newUrl) {
        //FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
        response.delegateObj().headers().set(LOCATION, newUrl);
        //getContext().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

	protected void sendError(HttpResponseStatus status) {
        //FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer("Failure:" + status.toString() + "\r\n", CharsetUtil.UTF_8));
        response.delegateObj().headers().set(CONTENT_TYPE, "text/plain;charset=UTF-8");
        //getContext().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

	protected void writeHtmlResponse(String msg, boolean forceClose) {
		ByteBuf buf = copiedBuffer(msg, CharsetUtil.UTF_8);
		//FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);

//		response.delegateObj().headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
//		response.delegateObj().headers().set(CONTENT_LENGTH, buf.readableBytes());
//		response.delegateObj().setStatus(HttpResponseStatus.OK);
		
		FullHttpResponse new_response = response.delegateObj().replace(buf);
		new_response.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
		new_response.headers().set(CONTENT_LENGTH, buf.readableBytes());
		new_response.setStatus(HttpResponseStatus.OK);
		this.response.replaceDelegateObj(new_response);
		
		//getContext().channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
	
	protected void writeJsonResponse(String json, boolean forceClose) {
		ByteBuf buf = copiedBuffer(json, CharsetUtil.UTF_8);
		//FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);

		//response.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");
		//response.headers().set(CONTENT_LENGTH, buf.readableBytes());
		
		
		
		FullHttpResponse new_response = response.delegateObj().replace(buf);
		new_response.headers().set(CONTENT_TYPE, "text/json; charset=UTF-8");
		new_response.headers().set(CONTENT_LENGTH, buf.readableBytes());
		new_response.setStatus(HttpResponseStatus.OK);
		this.response.replaceDelegateObj(new_response);
		
		//getContext().channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
	
	protected void writeResponse(HttpResponseStatus status, JCHttpResponse response, boolean forceClose) {
		//HttpResponse deleg_obj = response.delegateObj();
		//getContext().channel().writeAndFlush(deleg_obj).addListener(ChannelFutureListener.CLOSE);
	}
}
