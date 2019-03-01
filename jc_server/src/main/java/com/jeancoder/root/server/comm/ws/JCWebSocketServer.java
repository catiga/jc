package com.jeancoder.root.server.comm.ws;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jc.proto.conf.ServerMod;
import com.jeancoder.root.server.inet.JCServer;
import com.jeancoder.root.server.inet.ServerCode;
import com.jeancoder.root.server.inet.ServerImpl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

public class JCWebSocketServer extends ServerImpl implements JCServer {

	private static Logger logger = LoggerFactory.getLogger(JCWebSocketServer.class);

	boolean ssl = false;
	
	private JCWebSocketServer() {
		this.modconf = new ServerMod();
		this.modconf.setId("id");
		this.modconf.setProxy_entry("entry");
		this.modconf.setProxy_path("/");
		this.modconf.setName("default server");
		this.modconf.setPort(12345);
		this.modconf.setScheme(ServerCode.WS.toString());
	}

	public JCWebSocketServer(ServerMod mod) {
		this.modconf = mod;
	}

	@Override
	public ServerCode defServerCode() {
		return ServerCode.WS;
	}

	public void start() {
		EventLoopGroup accGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			final SslContext sslCtx;
	        if (ssl) {
	            SelfSignedCertificate ssc = new SelfSignedCertificate();
	            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
	        } else {
	            sslCtx = null;
	        }
			super.start();
			final ServerBootstrap server = new ServerBootstrap();
			server.group(accGroup, workerGroup).channel(NioServerSocketChannel.class)
				.handler(new LoggingHandler(LogLevel.INFO))
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3 * 1000)// 连接超时，单位毫秒
				.option(ChannelOption.SO_BACKLOG, 1024) // 连接队列长度
				.option(ChannelOption.SO_REUSEADDR, true)
				.childOption(ChannelOption.SO_KEEPALIVE, true)
				.childOption(ChannelOption.TCP_NODELAY, true)
				.childHandler(new WebSocketChannelInitaializer(sslCtx));
			 
			int port = modconf.getPort();
            Channel channel = server.bind(new InetSocketAddress(port)).sync().channel();
            //关闭连接
            channel.closeFuture().sync();
		} catch (Exception e) {
			logger.error("http server start error:", e);
		} finally {
			accGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	public static void main(String[] argc) throws InterruptedException {
		System.out.println(System.getProperty("java.class.path"));
		JCServer server = new JCWebSocketServer();
		server.start();
	}

}
