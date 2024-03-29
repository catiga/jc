package com.jeancoder.root.server.comm.http;

import static com.jeancoder.root.io.line.HeaderNames.CONNECTION;
import static com.jeancoder.root.io.line.HeaderNames.CONTENT_LENGTH;
import static com.jeancoder.root.io.line.HeaderNames.CONTENT_TYPE;
import static com.jeancoder.root.io.line.HeaderValues.KEEP_ALIVE;
import static io.netty.buffer.Unpooled.copiedBuffer;

import java.net.InetSocketAddress;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jc.proto.conf.AppMod;
import com.jc.proto.msg.ReplyMsg;
import com.jc.proto.msg.ReplyServerBody;
import com.jc.proto.msg.ct.UpgradeMsg;
import com.jeancoder.root.env.ChannelContextWrapper;
import com.jeancoder.root.exception.Code404Exception;
import com.jeancoder.root.exception.Code500Exception;
import com.jeancoder.root.exception.CompileException;
import com.jeancoder.root.exception.PrivilegeException;
import com.jeancoder.root.exception.RunningException;
import com.jeancoder.root.io.http.JCHttpRequest;
import com.jeancoder.root.io.http.JCHttpResponse;
import com.jeancoder.root.manager.JCVMDelegator;
import com.jeancoder.root.server.comm.ws.WebSocketHandler;
import com.jeancoder.root.server.inet.JCServer;
import com.jeancoder.root.server.state.GlobalStateHolder;
import com.jeancoder.root.server.state.RequestStateHolder;
import com.jeancoder.root.server.state.RequestStateModel;
import com.jeancoder.root.server.state.ServerHolder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

public class DispatcherHandler extends SimpleChannelInboundHandler<HttpObject> {

	private static Logger logger = LoggerFactory.getLogger(DispatcherHandler.class);
	
	protected HttpRequest request;
	protected HttpHeaders headers;

	protected boolean readingChunks;

//	private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); // Disk

	private HttpPostRequestDecoder decoder;
	
	private static final String FAVICON_ICO = "/favicon.ico";
	private static final String ERROR = "error";
	private static final String SUCCESS = "success";
	
	private RequestStateModel requestModel;
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug(ctx.channel().id().toString() + " is actived");
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug(ctx.channel().id().toString() + " is closed");
		}
		JCVMDelegator.releaseContext();
		if (decoder != null) {
			decoder.cleanFiles();
		}
	}
	
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
    	logger.error(e.getMessage(), e);
    	ctx.channel().close();
    }
	
	static int i = 0;
	
	protected void messageReceived(ChannelHandlerContext ctx, HttpRequest requestObj) {
		boolean keepAlive = HttpUtil.isKeepAlive(requestObj);
		
		HttpResponseStatus default_res_status = HttpResponseStatus.OK;
		HttpMethod METHOD = requestObj.method();
		if(METHOD==HttpMethod.OPTIONS) {
			default_res_status = HttpResponseStatus.NO_CONTENT;
		}
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, default_res_status);
		
		InetSocketAddress remote = (InetSocketAddress)ctx.channel().remoteAddress();
		JCHttpRequest stand_request = null; JCHttpResponse stand_response = null;
		//打印所有request header
//		logger.info(requestObj.toString());
//		logger.info(requestObj.headers().toString());
		try {
			stand_request = new JCHttpRequest((FullHttpRequest)request);
			stand_request.setRemoteHost(remote);
			stand_response = new JCHttpResponse(response);
			JCVMDelegator.delegate().getVM().dispatch(stand_request, stand_response);
			//requestModel.setResTime(Calendar.getInstance().getTimeInMillis());
			requestModel.setStatusCode(200);
		} catch(Exception e) {
			logger.error("so should send msg by socket to center server:" + e.getMessage(), e);
			processHandlerException(e, stand_request, stand_response);
		} finally {
			JCVMDelegator.releaseContext();
			if(!keepAlive) {
				if(stand_response!=null&&stand_response.delegateObj()!=null)
					ctx.writeAndFlush(stand_response.delegateObj()).addListener(ChannelFutureListener.CLOSE);
				else
					ctx.flush();
			} else {
				stand_response.delegateObj().headers().set(CONNECTION, KEEP_ALIVE);
                ctx.write(stand_response.delegateObj());
			}
		}
    }
	
	protected JCHttpResponse messageReceivedAsync(ChannelHandlerContext ctx, HttpRequest requestObj) {
		HttpResponseStatus default_res_status = HttpResponseStatus.OK;
		HttpMethod METHOD = requestObj.method();
		if(METHOD==HttpMethod.OPTIONS) {
			default_res_status = HttpResponseStatus.NO_CONTENT;
		}
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, default_res_status);
		
		InetSocketAddress remote = (InetSocketAddress)ctx.channel().remoteAddress();
		JCHttpRequest stand_request = null; JCHttpResponse stand_response = null;
		try {
			stand_request = new JCHttpRequest((FullHttpRequest)request);
			stand_request.setRemoteHost(remote);
			stand_response = new JCHttpResponse(response);
			JCVMDelegator.delegate().getVM().dispatch(stand_request, stand_response);
			requestModel.setStatusCode(200);
		} catch(Exception e) {
			logger.error("so should send msg by socket to center server:" + e.getMessage());
			processHandlerException(e, stand_request, stand_response);
		} finally {
			JCVMDelegator.releaseContext();
		}
		return stand_response;
    }
	
	private void writeResponse(ChannelHandlerContext ctx, HttpResponseStatus status, String msg, boolean forceClose) {
		ByteBuf buf = copiedBuffer(msg, CharsetUtil.UTF_8);
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, buf);

		response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
		response.headers().set(CONTENT_LENGTH, buf.readableBytes());
		ctx.channel().writeAndFlush(response);
	}
	
	private void writeResponse(ChannelHandlerContext ctx, HttpResponseStatus status, String content_type, String msg, boolean forceClose) {
		ByteBuf buf = copiedBuffer(msg, CharsetUtil.UTF_8);
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, buf);

		response.headers().set(CONTENT_TYPE, content_type + "; charset=UTF-8");
		response.headers().set(CONTENT_LENGTH, buf.readableBytes());
		ctx.channel().writeAndFlush(response);
	}
	
//	protected void dealWithContentType() throws Exception {
//		String contentType = getContentType();
//		if (contentType.equals("application/json")) { // 可以使用HttpJsonDecoder
//			String jsonStr = ((FullHttpRequest)request).content().toString(Charsets.toCharset(CharEncoding.UTF_8));
//			JSONObject obj = JSON.parseObject(jsonStr);
//			for (Entry<String, Object> item : obj.entrySet()) {
//				System.out.println(item.getKey() + "=" + item.getValue().toString());
//			}
//
//		} else if (contentType.equals("application/x-www-form-urlencoded")) {
//			initPostRequestDecoder();
//			List<InterfaceHttpData> datas = decoder.getBodyHttpDatas();
//			for (InterfaceHttpData data : datas) {
//				if (data.getHttpDataType() == HttpDataType.Attribute) {
//					Attribute attribute = (Attribute) data;
//					System.out.println(attribute.getName() + "=" + attribute.getValue());
//				}
//			}
//
//		} else if (contentType.equals("multipart/form-data")) { // 用于文件上传
//			readHttpDataAllReceive();
//		} else {
//			// do nothing...
//		}
//	}
	
//	private void readHttpDataAllReceive() throws Exception {
//		initPostRequestDecoder();
//		try {
//			List<InterfaceHttpData> datas = decoder.getBodyHttpDatas();
//			for (InterfaceHttpData data : datas) {
//				writeHttpData(data);
//			}
//		} catch (Exception e) {
//			// 此处仅简单抛出异常至上一层捕获处理，可自定义处理
//			throw new Exception(e);
//		}
//	}

//	private void writeHttpData(InterfaceHttpData data) throws Exception {
//		// 后续会加上块传输（HttpChunk），目前仅简单处理
//		if (data.getHttpDataType() == HttpDataType.FileUpload) {
//			FileUpload fileUpload = (FileUpload) data;
//			String fileName = fileUpload.getFilename();
//			if (fileUpload.isCompleted()) {
//				// 保存到磁盘
//				StringBuffer fileNameBuf = new StringBuffer();
//				fileNameBuf.append(DiskFileUpload.baseDirectory).append(fileName);
//				fileUpload.renameTo(new File(fileNameBuf.toString()));
//			}
//		}
//	}
	
//	private String getContentType() {
//		String typeStr = headers.get("Content-Type").toString();
//		String[] list = typeStr.split(";");
//		return list[0];
//	}
	
	protected void processHandlerException(Throwable e, JCHttpRequest req, JCHttpResponse res) {
		StringBuffer err_for_master = new StringBuffer();
		StringBuffer error_buffer = new StringBuffer();
		
		error_buffer.append("VM ID:" + JCVMDelegator.delegate().delegatedId() + "\r\n\r\n");
		err_for_master.append("VM ID:" + JCVMDelegator.delegate().delegatedId() + "\r\n");
		if(e instanceof RunningException) {
			RunningException rex = (RunningException)e;
			logger.error(rex.getApp() + "..." + rex.getPath() + "&&&" + rex.getRes());
			error_buffer.append("JCAPP CODE:" + rex.getApp() + "\r\n");
			error_buffer.append("JCAPP PATH:" + rex.getPath() + "\r\n");
			error_buffer.append("JCAPP RES:" + rex.getRes() + "\r\n\r\n");
			
			err_for_master.append("JCAPP CODE:" + rex.getApp() + "\r\n");
			err_for_master.append("JCAPP PATH:" + rex.getPath() + "\r\n");
			err_for_master.append("JCAPP RES:" + rex.getRes() + "\r\n");
		}
		error_buffer.append(e.getMessage() + "\r\n\r\n");
		for(StackTraceElement ste : e.getCause()==null?e.getStackTrace():e.getCause().getStackTrace()) {
			if(ste.getClassName().indexOf("io.netty.")>-1) {
				break;
			}
			error_buffer.append("  at " + ste.getClassName() + "(" + ste.getFileName() + ":" + ste.getLineNumber() + ")\r\n\r\n");
			err_for_master.append("	at " + ste.getClassName() + "(" + ste.getFileName() + ":" + ste.getLineNumber() + ")\r\n");
		}
		ByteBuf buf = copiedBuffer(error_buffer.toString().getBytes());
		FullHttpResponse new_response = null;
		if(res!=null&&res.delegateObj()!=null) {
			new_response = res.delegateObj().replace(buf);
		} else {
			new_response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
		}
		new_response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
		new_response.headers().set(CONTENT_LENGTH, buf.readableBytes());
		
		HttpResponseStatus res_code = HttpResponseStatus.BAD_REQUEST;
		if(e instanceof Code404Exception) {
			res_code = HttpResponseStatus.NOT_FOUND;
		} else if(e instanceof Code500Exception) {
			res_code = HttpResponseStatus.INTERNAL_SERVER_ERROR;
		} else if(e instanceof PrivilegeException) {
			res_code = HttpResponseStatus.FORBIDDEN;
		} else if(e instanceof CompileException) {
			res_code = HttpResponseStatus.BAD_REQUEST;
		}
		new_response.setStatus(res_code);
		res.replaceDelegateObj(new_response);
		requestModel.setStatusCode(res_code.code());
		requestModel.setErrInfo(err_for_master.toString());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		// TODO Auto-generated method stub
		super.channelRead(ctx, msg);
	}
	
	
	
	
	WebSocketServerHandshaker handshaker;
    
    protected String getWebSocketURL(HttpRequest req) {
        System.out.println("Req URI : " + req.uri());
        String url =  "ws://" + req.headers().get("Host") + req.uri();
        System.out.println("Constructed URL : " + url);
        return url;
    }
    
    protected void handleHandshake(ChannelHandlerContext ctx, HttpRequest req) {
        WebSocketServerHandshakerFactory wsFactory =
                new WebSocketServerHandshakerFactory(getWebSocketURL(req), null, true);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
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
		
		if (uri_path.equals(FAVICON_ICO)) {
			writeResponse(ctx, HttpResponseStatus.ACCEPTED, "unsupported", true);
			return;
		}
		HttpHeaders headers_obj = ((HttpRequest)requestObj).headers();
		if ("Upgrade".equalsIgnoreCase(headers_obj.get(HttpHeaderNames.CONNECTION)) &&
                "WebSocket".equalsIgnoreCase(headers_obj.get(HttpHeaderNames.UPGRADE))) {

            //Adding new handler to the existing pipeline to handle WebSocket Messages
            ctx.pipeline().replace(this, "websocketHandler", new WebSocketHandler(requestObj));

            logger.info("WebSocketHandler added to the pipeline");
            logger.info("Opened Channel : " + ctx.channel());
            logger.info("Handshaking....");
            //Do the Handshake to upgrade connection from HTTP to WebSocket protocol
            handleHandshake(ctx, (HttpRequest)requestObj);
            logger.info("Handshake is done");
            return;
        }
		
		if(uri_path.equals("/TESTJC")) {
			String result = SUCCESS + ":welcome msg!------From JC Server";
			writeResponse(ctx, HttpResponseStatus.OK, result, true);
			return;
		} else if(uri_path.startsWith("/TESTJC")) {
			if(uri_path.indexOf("?")>-1) {
				QueryStringDecoder query = new QueryStringDecoder(uri_path);
				String command = null;
				if(query.parameters().get("command")!=null&&!query.parameters().get("command").isEmpty()) {
					command = query.parameters().get("command").get(0);
				}
				if(command!=null) {
					if(command.equals("ss")) {
						StringBuilder view_result = new StringBuilder();
						view_result.append("<html><head></head>");
						view_result.append("<body>");
						Enumeration<JCServer> all_running_servers = ServerHolder.getHolder().servers();
						while(all_running_servers.hasMoreElements()) {
							JCServer jcs = all_running_servers.nextElement();
							view_result.append("<div>" + jcs.defServerCode() + ":" + jcs.serverId() + "</div><br/>");
						}
						view_result.append("</body></html>");
						
						writeResponse(ctx, HttpResponseStatus.OK, "text/html", view_result.toString(), true);
						return;
					} else if(command.equals("cs")) {
						StringBuilder view_result = new StringBuilder();
						view_result.append("<html><head></head>");
						view_result.append("<body>");
						List<String> all_running_servers = ServerHolder.getHolder().dispatchlist();
						for(String s : all_running_servers) {
							view_result.append("<div>" + s + "</div><br/>");
							SocketChannel channel = (SocketChannel)ServerHolder.getHolder().dispatchaim(s);
							if(channel!=null) {
								ReplyMsg msg = new ReplyMsg();
								ReplyServerBody body = new ReplyServerBody("center server reply:" + "client_id=" + s + " 推送命令");
								msg.setBody(body);
								channel.writeAndFlush(msg);
							}
						}
						view_result.append("</body></html>");
						
						writeResponse(ctx, HttpResponseStatus.OK, "text/html", view_result.toString(), true);
						return;
					} else if(command.equals("upgrade")) {
						String clsid = query.parameters().get("clsid").get(0);
						String appid = query.parameters().get("appid").get(0);
						
						StringBuilder view_result = new StringBuilder();
						view_result.append("<html><head></head>");
						view_result.append("<body>");
						
						view_result.append("<div>" + clsid + "</div><br/>");
						SocketChannel channel = (SocketChannel)ServerHolder.getHolder().dispatchaim(clsid);
						if(channel!=null) {
							AppMod am = new AppMod();
							am.setApp_id(appid);
							UpgradeMsg msg = new UpgradeMsg(am);
							channel.writeAndFlush(msg);
						}
						
						view_result.append("</body></html>");
						
						writeResponse(ctx, HttpResponseStatus.OK, "text/html", view_result.toString(), true);
						return;
					}
				}
			}
		}
		
		requestModel = new RequestStateModel(request); InetSocketAddress remote = (InetSocketAddress)ctx.channel().remoteAddress();
		requestModel.init(request, remote);
		headers = request.headers();
		
//		JCVMDelegator.bindContext(ChannelContextWrapper.newone(ctx));
//		messageReceived(ctx, request);
		
		//** new one
		JCHttpResponse stand_response = null;  FullHttpResponse response = null;
        boolean keepAlive = HttpUtil.isKeepAlive(request);
		
		FutureTask<JCHttpResponse> future = new FutureTask<JCHttpResponse>(new VisCall(ChannelContextWrapper.newone(ctx), request, this));
		
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(future); //执行
        
        try {
        	if(GlobalStateHolder.INSTANCE.getInternalExecuteTimeout()!=null && GlobalStateHolder.INSTANCE.getInternalExecuteTimeout()>0L) {
        		stand_response = future.get(GlobalStateHolder.INSTANCE.getInternalExecuteTimeout(), TimeUnit.MILLISECONDS); //async timeout setting
        	} else {
        		stand_response = future.get();		//sync get back response
        	}
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
        	logger.error("IET:", e);
            future.cancel(true);
			requestModel.setStatusCode(HttpResponseStatus.REQUEST_TIMEOUT.code());
            String msg = "request_interrupted_for_timeout";
			ByteBuf buf = copiedBuffer(msg, CharsetUtil.UTF_8);
			response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.REQUEST_TIMEOUT, buf);
			response.headers().set(CONTENT_TYPE, "text/plain" + "; charset=UTF-8");
			response.headers().set(CONTENT_LENGTH, buf.readableBytes());
        } finally {
        	try {
	        	requestModel.setResTime(Calendar.getInstance().getTimeInMillis());		//set response timestamp
	            executor.shutdown();
	            RequestStateHolder INSTANCE = RequestStateHolder.getInstance();
//	            INSTANCE.add(requestModel);
	            INSTANCE.addNew(requestModel);
        	}catch(Exception e) {
        		logger.error("DISPATCH_SHUTDOWN_EXCEPTION:", e);
        	}
            
            if(!keepAlive) {
				if(stand_response!=null&&stand_response.delegateObj()!=null) {
					ctx.writeAndFlush(stand_response.delegateObj()).addListener(ChannelFutureListener.CLOSE);
				}
				else {
					if(response==null)
						ctx.flush();
					else 
						ctx.writeAndFlush(response);
				}
			} else {
				if(stand_response!=null&&stand_response.delegateObj()!=null) {
					stand_response.delegateObj().headers().set(CONNECTION, KEEP_ALIVE);
					ctx.write(stand_response.delegateObj());
				} else {
					if(response!=null)
						ctx.write(response);
				}
			}
        }
	}
	
	class VisCall implements Callable<JCHttpResponse> {

		ChannelContextWrapper context = null;
		DispatcherHandler handler = null;
		HttpRequest request = null;
		
		VisCall(ChannelContextWrapper context, HttpRequest request, DispatcherHandler handler) {
			this.context = context;
			this.handler = handler;
			this.request = request;
		}
		
		@Override
		public JCHttpResponse call() throws Exception {
			JCVMDelegator.bindContext(context);
			return handler.messageReceivedAsync(context.getContext(), request);
		}
		
	}
}
