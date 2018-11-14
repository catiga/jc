package com.jeancoder.root.server.comm.socket;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeancoder.root.server.inet.JCServer;
import com.jeancoder.root.server.inet.ServerCode;
import com.jeancoder.root.server.inet.ServerImpl;
import com.jeancoder.root.server.proto.conf.ServerMod;
import com.jeancoder.root.server.proto.msg.AskMsg;
import com.jeancoder.root.server.state.NettyChannelMap;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class JCSocketServer extends ServerImpl implements JCServer {

	private static Logger logger = LoggerFactory.getLogger(JCSocketServer.class);
	
	public JCSocketServer() {
		this.modconf = new ServerMod();
		this.modconf.setProxy_entry("entry");
		this.modconf.setProxy_path("/");
		this.modconf.setServer_name("default server");
		this.modconf.setServer_port(12346);
		this.modconf.setServer_scheme(ServerCode.SOCKET.toString());
	}
	
	public JCSocketServer(ServerMod mod) {
		this.modconf = mod;
	}
	
	@Override
	public ServerCode defServerCode() {
		return ServerCode.SOCKET;
	}
	
	public void start() {
		EventLoopGroup accGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			final ServerBootstrap server = new ServerBootstrap();

			server.group(accGroup, workerGroup) // 组装NioEventLoopGroup
					.channel(NioServerSocketChannel.class) // 设置channel类型为NIO类型
					.option(ChannelOption.SO_BACKLOG, 1024) // 连接队列长度
					.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
					.childOption(ChannelOption.TCP_NODELAY, true).childOption(ChannelOption.SO_KEEPALIVE, true) // 保持长连接
					.childOption(ChannelOption.SO_REUSEADDR, true)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) {
							InetSocketAddress remote = ch.remoteAddress();
							String remote_host_name = remote.getHostName();
							String remote_host_address = remote.getAddress().getHostAddress();
							int remote_port = remote.getPort();
							logger.info("connection msg: " + remote_host_name + ", address=" + remote_host_address + ":" + remote_port + " connected");
							logger.debug("IP:" + ch.localAddress().getHostName());
							logger.debug("Port:" + ch.localAddress().getPort());

							// 配置入站、出站事件channel
							ChannelPipeline pipeline = ch.pipeline();

							//pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
							pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0,  2));
							pipeline.addLast(new ObjectEncoder());
							pipeline.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));

							pipeline.addLast(new GeneralLiveHandler());
						}
					});
			int port = modconf.getServer_port();
			ChannelFuture channelFuture = server.bind(port).sync();
			if (channelFuture.isSuccess()) {
				SocketAddress net_address = channelFuture.channel().localAddress();
				logger.info(defServerCode() + " SERVER STARTED: " + net_address);
			}
			//channelFuture.channel().closeFuture().sync();
		} catch (Exception e) {
			logger.error("service start error:", e);
		} finally {
			//accGroup.shutdownGracefully();
			//workerGroup.shutdownGracefully();
		}
	}

	public static void main(String[] argc) throws InterruptedException {
		JCServer server = new JCSocketServer();
		System.out.println("服务准备启动");
		server.start();
		logger.info("服务启动成功");
		while (true) {
			SocketChannel channel = (SocketChannel) NettyChannelMap.get("001");
			if (channel != null) {
				AskMsg askMsg = new AskMsg();
				channel.writeAndFlush(askMsg);
			}
			TimeUnit.SECONDS.sleep(10);
		}
	}
}
