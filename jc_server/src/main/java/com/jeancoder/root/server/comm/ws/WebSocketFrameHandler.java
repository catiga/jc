package com.jeancoder.root.server.comm.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;


/**
 *
 */
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<Object> {    
        
    private static final Logger logger = LoggerFactory.getLogger(WebSocketFrameHandler.class);
    
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
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
    	if(msg instanceof HttpRequest) {
    		HttpRequest httpRequest = (HttpRequest) msg;

            System.out.println("Http Request Received");

            HttpHeaders headers = httpRequest.headers();
            System.out.println("Connection : " +headers.get("Connection"));
            System.out.println("Upgrade : " + headers.get("Upgrade"));

            if ("Upgrade".equalsIgnoreCase(headers.get(HttpHeaderNames.CONNECTION)) &&
                    "WebSocket".equalsIgnoreCase(headers.get(HttpHeaderNames.UPGRADE))) {

                //Adding new handler to the existing pipeline to handle WebSocket Messages
                ctx.pipeline().replace(this, "websocketHandler", new WebSocketHandler(null));

                System.out.println("WebSocketHandler added to the pipeline");
                System.out.println("Opened Channel : " + ctx.channel());
                System.out.println("Handshaking....");
                //Do the Handshake to upgrade connection from HTTP to WebSocket protocol
                handleHandshake(ctx, httpRequest);
                System.out.println("Handshake is done");
            }
    	} else {
    		System.out.println("Incoming request is unknown");
    	}
    }
    
    @Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		// TODO Auto-generated method stub
		super.channelRead(ctx, msg);
	}

	@Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
        String channelId = ctx.channel().id().asLongText();
        logger.info("websocket channel active: " + channelId);
        
        if(ctx.channel().isActive()) {
        	System.out.println("actived");
        }
        if(ctx.channel().isOpen()) {
        	System.out.println("opened");
        }
        System.out.println("kexieruma" + ctx.channel().isWritable());
        ctx.channel().writeAndFlush("openok");
        
//        if(ServerChannelMgmt.getUserChannelMap().get(channelId) == null){
//            ServerChannelMgmt.getUserChannelMap().put(channelId, ctx.channel());
//        }
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String channelId = ctx.channel().id().asLongText();
        logger.info("websocket channel inactive: " + channelId);
//        if(ServerChannelMgmt.getUserChannelMap().get(channelId) != null){
//            ServerChannelMgmt.getUserChannelMap().remove(channelId);
//        }
        
        ctx.fireChannelInactive();
    }
    
    @Override  
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {  
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {  
            IdleStateEvent event = (IdleStateEvent) evt;  
            if (event.state() == IdleState.READER_IDLE) {  
                ChannelFuture f = ctx.channel().writeAndFlush(new TextWebSocketFrame("iTker: 您长时间没有咨询了，再见！ 若有需求，欢迎您随时与我们联系！"));
                f.addListener(ChannelFutureListener.CLOSE);
            }    
            else if (event.state() == IdleState.WRITER_IDLE)  
                System.out.println("write idle");  
            else if (event.state() == IdleState.ALL_IDLE)  
                System.out.println("all idle");  
        }  
    }  
}
