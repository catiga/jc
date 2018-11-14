package com.jeancoder.root.server.comm.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeancoder.root.server.comm.socket.JCSocketServer;
import com.jeancoder.root.server.inet.JCServer;
import com.jeancoder.root.server.inet.ServerCode;
import com.jeancoder.root.server.inet.ServerImpl;
import com.jeancoder.root.server.proto.conf.ServerMod;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class JCHttpServer extends ServerImpl implements JCServer {

	private static Logger logger = LoggerFactory.getLogger(JCSocketServer.class);

	public JCHttpServer() {
		this.modconf = new ServerMod();
		this.modconf.setProxy_entry("entry");
		this.modconf.setProxy_path("/");
		this.modconf.setServer_name("default server");
		this.modconf.setServer_port(12345);
		this.modconf.setServer_scheme(ServerCode.HTTP.toString());
	}
	
	public JCHttpServer(ServerMod mod) {
		this.modconf = mod;
	}
	
	@Override
	public ServerCode defServerCode() {
		return ServerCode.HTTP;
	}

	public void start() {
		EventLoopGroup accGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			final ServerBootstrap server = new ServerBootstrap();

			server.group(accGroup, workerGroup) // 组装NioEventLoopGroup
					.channel(NioServerSocketChannel.class) // 设置channel类型为NIO类型
					.option(ChannelOption.SO_BACKLOG, 1024) // 连接队列长度
					.childOption(ChannelOption.SO_KEEPALIVE, true).childOption(ChannelOption.TCP_NODELAY, true)
					.childOption(ChannelOption.SO_REUSEADDR, true)
					.childHandler(new ChannelInitializer<NioSocketChannel>() {
						@Override
						protected void initChannel(NioSocketChannel ch) {
							// 配置入站、出站事件channel
							ChannelPipeline pipeline = ch.pipeline();
							// 编解码的处理
							pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
							pipeline.addLast(new LengthFieldPrepender(4));
							pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
							pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));

							// 业务处理器
							pipeline.addLast(new StringHandler());
						}
					});
			
			int port = modconf.getServer_port();
			ChannelFuture channelFuture = server.bind(port).sync();
			channelFuture.channel().closeFuture().sync();
		} catch (Exception e) {
			logger.error("http server start error:", e);
		} finally {
			accGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

}
