package com.jeancoder.root.server.ugr;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jc.proto.msg.Constants;
import com.jc.proto.msg.LoginMsg;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;

public class NettyClientBootstrap {
	
	private static Logger logger = LoggerFactory.getLogger(NettyClientBootstrap.class.getName());
	
	private int port;
	
	private String host;
	
	private SocketChannel socketChannel;
	
	private final ClientIdleStateTrigger idleStateTrigger = new ClientIdleStateTrigger();

	public NettyClientBootstrap(int port, String host) throws InterruptedException {
		this.port = port;
		this.host = host;
		start();
	}

	private void start() throws InterruptedException {
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.group(eventLoopGroup);
		bootstrap.remoteAddress(host, port);
		bootstrap.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel socketChannel) throws Exception {
				//socketChannel.pipeline().addLast(new IdleStateHandler(20, 10, 0));
				socketChannel.pipeline().addLast(new IdleStateHandler(0, 10, 0, TimeUnit.SECONDS));
				socketChannel.pipeline().addLast(idleStateTrigger);
				socketChannel.pipeline().addLast(new ObjectEncoder());
				socketChannel.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
				socketChannel.pipeline().addLast(new NettyClientHandler());
				socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 2));
			}
		});
		ChannelFuture future = bootstrap.connect(host, port).sync();
		if (future.isSuccess()) {
			socketChannel = (SocketChannel) future.channel();
			logger.info("master server connect success.---------");
		}
	}

	public static void main(String[] args) throws InterruptedException {
		Constants.setClientId("001");
		NettyClientBootstrap bootstrap = new NettyClientBootstrap(8091, "localhost");

		LoginMsg loginMsg = new LoginMsg();
		loginMsg.setPassword("yao");
		loginMsg.setUserName("robin");
		bootstrap.socketChannel.writeAndFlush(loginMsg);
//		while (true) {
//			TimeUnit.SECONDS.sleep(3);
//			AskMsg askMsg = new AskMsg();
//			AskParams askParams = new AskParams();
//			askParams.setAuth("authToken");
//			askMsg.setParams(askParams);
//			bootstrap.socketChannel.writeAndFlush(askMsg);
//		}
	}
}
