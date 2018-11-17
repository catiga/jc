package com.jeancoder.root.server.comm.http;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

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
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class JCHttpServer extends ServerImpl implements JCServer {

	private static Logger logger = LoggerFactory.getLogger(JCSocketServer.class);
	
	boolean ssl = false;

	public JCHttpServer() {
		this.modconf = new ServerMod();
		this.modconf.setProxy_entry("entry");
		this.modconf.setProxy_path("/");
		this.modconf.setName("default server");
		this.modconf.setPort(12345);
		this.modconf.setScheme(ServerCode.HTTP.toString());
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
			super.start();
			final ServerBootstrap server = new ServerBootstrap();
			server.group(accGroup, workerGroup) // 组装NioEventLoopGroup
					.channel(NioServerSocketChannel.class) // 设置channel类型为NIO类型
					.option(ChannelOption.SO_BACKLOG, 1024) // 连接队列长度
					.childOption(ChannelOption.SO_KEEPALIVE, true).childOption(ChannelOption.TCP_NODELAY, true)
					.childOption(ChannelOption.SO_REUSEADDR, true)
					.childHandler(new ChannelInitializer<NioSocketChannel>() {
						@Override
						protected void initChannel(NioSocketChannel ch) {
//							ch.pipeline().addLast("codec", new HttpServerCodec());
//							ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
//							ch.pipeline().addLast("handler", new JCHttpHandler()); // 业务handler
							
							ch.pipeline().addLast("decoder", new HttpRequestDecoder());
							ch.pipeline().addLast("encoder", new HttpResponseEncoder());
							ch.pipeline().addLast("deflater", new HttpContentCompressor());

							ch.pipeline().addLast("handler", new JcMultiPartHandler()); // 业务handler
						}
					});

			int port = modconf.getPort();
			ChannelFuture channelFuture = server.bind(new InetSocketAddress(port)).sync();
			
			if (channelFuture.isSuccess()) {
				SocketAddress net_address = channelFuture.channel().localAddress();
				logger.info("jc server start success:" + net_address);
			}
			channelFuture.channel().closeFuture().sync();
		} catch (Exception e) {
			logger.error("http server start error:", e);
		} finally {
			accGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	public static void main(String[] argc) throws InterruptedException {
		System.out.println(System.getProperty("java.class.path"));
		JCServer server = new JCHttpServer();
		server.start();
	}

}
