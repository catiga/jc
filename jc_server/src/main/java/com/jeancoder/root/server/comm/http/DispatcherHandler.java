package com.jeancoder.root.server.comm.http;

import static com.jeancoder.root.io.line.HeaderNames.CONTENT_LENGTH;
import static com.jeancoder.root.io.line.HeaderNames.CONTENT_TYPE;
import static com.jeancoder.root.io.line.HeaderNames.CONNECTION;
import static com.jeancoder.root.io.line.HeaderValues.KEEP_ALIVE;
import static io.netty.buffer.Unpooled.copiedBuffer;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jeancoder.root.exception.RunningException;
import com.jeancoder.root.io.http.JCHttpRequest;
import com.jeancoder.root.io.http.JCHttpResponse;
import com.jeancoder.root.manager.JCVMDelegator;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

public class DispatcherHandler extends SimpleChannelInboundHandler<HttpObject> {

	private static Logger logger = LoggerFactory.getLogger(DispatcherHandler.class);
	
	private HttpRequest request;
	private HttpHeaders headers;

	protected boolean readingChunks;

	private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); // Disk

	private HttpPostRequestDecoder decoder;
	
	private static final String FAVICON_ICO = "/favicon.ico";
	private static final String ERROR = "error";
	private static final String SUCCESS = "success";
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		JCVMDelegator.releaseContext();
		ctx.flush();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug(ctx.channel().id().toString() + " is actived");
		} else {
			logger.info(ctx.channel().id().toString() + " is actived");
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug(ctx.channel().id().toString() + " is closed");
		} else {
			logger.info(ctx.channel().id().toString() + " is closed");
		}
		JCVMDelegator.releaseContext();
		if (decoder != null) {
			decoder.cleanFiles();
		}
	}
	
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
    	logger.error(e.getMessage(),e);
    	ctx.channel().close();
    }
	
	static int i = 0;
	
	protected void messageReceived(ChannelHandlerContext ctx, HttpRequest requestObj) {
		logger.info(requestObj.hashCode() + "---" + System.identityHashCode(requestObj));
		String uri_path = request.uri();
		if (uri_path.equals(FAVICON_ICO)) {
			return;
		}
		boolean keepAlive = HttpUtil.isKeepAlive(requestObj);
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		JCHttpResponse stand_response = new JCHttpResponse(response);
		InetSocketAddress remote = (InetSocketAddress)ctx.channel().remoteAddress();
		try {
			JCHttpRequest stand_request = new JCHttpRequest((FullHttpRequest)request);
			stand_request.setRemoteHost(remote);
			
			JCVMDelegator.delegate().getVM().dispatch(stand_request, stand_response);
		}catch(Exception e) {
			logger.error("so should send msg by socket to center server:", e);
			StringBuffer error_buffer = new StringBuffer();
			if(e instanceof RunningException) {
				RunningException rex = (RunningException)e;
				error_buffer.append("JCAPP CODE:" + rex.getApp() + "\r\n\r\n");
				error_buffer.append("JCAPP PATH:" + rex.getPath() + "\r\n\r\n");
				error_buffer.append("JCAPP RES:" + rex.getRes() + "\r\n\r\n");
			}
			error_buffer.append(e.getMessage() + "\r\n\r\n");
			for(StackTraceElement ste : e.getCause()==null?e.getStackTrace():e.getCause().getStackTrace()) {
				if(ste.getClassName().indexOf("io.netty.")>-1) {
					break;
				}
				error_buffer.append("	at " + ste.getClassName() + "(" + ste.getFileName() + ":" + ste.getLineNumber() + ")\r\n\r\n");
			}
			ByteBuf buf = copiedBuffer(error_buffer.toString().getBytes());
			FullHttpResponse new_response = stand_response.delegateObj().replace(buf);
			new_response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
			new_response.headers().set(CONTENT_LENGTH, buf.readableBytes());
			new_response.setStatus(HttpResponseStatus.BAD_REQUEST);
			stand_response.replaceDelegateObj(new_response);
		} finally {
			if(!keepAlive) {
				ctx.writeAndFlush(stand_response.delegateObj()).addListener(ChannelFutureListener.CLOSE);
			} else {
				stand_response.delegateObj().headers().set(CONNECTION, KEEP_ALIVE);
                ctx.write(stand_response.delegateObj());
			}
		}
		
//		if(stand_response.getStatus()==HttpResponseStatus.FOUND.code()) {
//			sendRedirect(ctx, stand_response.getHeader(LOCATION));
//		} else {
//			writeHtmlResponse(ctx, HttpResponseStatus.OK, html.toString(), true);
//		}
		//ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
	
	private void writeResponse(ChannelHandlerContext ctx, HttpResponseStatus status, String msg, boolean forceClose) {
		ByteBuf buf = copiedBuffer(msg, CharsetUtil.UTF_8);
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, buf);

		response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
		response.headers().set(CONTENT_LENGTH, buf.readableBytes());
		ctx.channel().writeAndFlush(response);
	}
	
	protected void dealWithContentType() throws Exception {
		String contentType = getContentType();
		if (contentType.equals("application/json")) { // 可以使用HttpJsonDecoder
			String jsonStr = ((FullHttpRequest)request).content().toString(Charsets.toCharset(CharEncoding.UTF_8));
			JSONObject obj = JSON.parseObject(jsonStr);
			for (Entry<String, Object> item : obj.entrySet()) {
				System.out.println(item.getKey() + "=" + item.getValue().toString());
			}

		} else if (contentType.equals("application/x-www-form-urlencoded")) {
			initPostRequestDecoder();
			List<InterfaceHttpData> datas = decoder.getBodyHttpDatas();
			for (InterfaceHttpData data : datas) {
				if (data.getHttpDataType() == HttpDataType.Attribute) {
					Attribute attribute = (Attribute) data;
					System.out.println(attribute.getName() + "=" + attribute.getValue());
				}
			}

		} else if (contentType.equals("multipart/form-data")) { // 用于文件上传
			readHttpDataAllReceive();
		} else {
			// do nothing...
		}
	}
	
	private void readHttpDataAllReceive() throws Exception {
		initPostRequestDecoder();
		try {
			List<InterfaceHttpData> datas = decoder.getBodyHttpDatas();
			for (InterfaceHttpData data : datas) {
				writeHttpData(data);
			}
		} catch (Exception e) {
			// 此处仅简单抛出异常至上一层捕获处理，可自定义处理
			throw new Exception(e);
		}
	}

	private void writeHttpData(InterfaceHttpData data) throws Exception {
		// 后续会加上块传输（HttpChunk），目前仅简单处理
		if (data.getHttpDataType() == HttpDataType.FileUpload) {
			FileUpload fileUpload = (FileUpload) data;
			String fileName = fileUpload.getFilename();
			if (fileUpload.isCompleted()) {
				// 保存到磁盘
				StringBuffer fileNameBuf = new StringBuffer();
				fileNameBuf.append(DiskFileUpload.baseDirectory).append(fileName);
				fileUpload.renameTo(new File(fileNameBuf.toString()));
			}
		}
	}
	
	private String getContentType() {
		String typeStr = headers.get("Content-Type").toString();
		String[] list = typeStr.split(";");
		return list[0];
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpObject requestObj) throws Exception {
		if (!(requestObj instanceof HttpRequest)) {		//discard invalid request
			String result = ERROR + ":unsupport request!------From JC Server";
			writeResponse(ctx, HttpResponseStatus.BAD_REQUEST, result, true);
			ReferenceCountUtil.release(requestObj);
			return;
		}
		HttpRequest request = this.request = (HttpRequest) requestObj;
		String uri_path = request.uri();
		if(uri_path.equals("/TESTJC")) {
			String result = SUCCESS + ":welcome msg!------From JC Server";
			writeResponse(ctx, HttpResponseStatus.OK, result, true);
			return;
		}
		headers = request.headers();
		messageReceived(ctx, request);
	}
	
	private void initPostRequestDecoder() {
		if (decoder != null) {
			decoder.cleanFiles();
			decoder = null;
		}
		decoder = new HttpPostRequestDecoder(factory, request, Charsets.toCharset(CharEncoding.UTF_8));
	}
	
}
