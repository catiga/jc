package com.jeancoder.root.server.comm.ws;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;

public class WebSocketChannelInitaializer extends ChannelInitializer<SocketChannel> {

	private final SslContext sslCtx;

	public WebSocketChannelInitaializer(SslContext sslCtx) {
		this.sslCtx = sslCtx;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();

		if (sslCtx != null) {
			pipeline.addLast(sslCtx.newHandler(ch.alloc()));
		}

		// // 添加超时处理
		pipeline.addLast(new IdleStateHandler(30, 0, 0));
		pipeline.addLast(new HttpServerCodec());
		//pipeline.addLast(new HttpObjectAggregator(65536));
		//pipeline.addLast(new WebSocketServerCompressionHandler());
//		pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH));
		pipeline.addLast(new WebSocketFrameHandler());
	}
}