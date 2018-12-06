package com.jeancoder.root.server.ugr;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jc.proto.conf.ServerMod;
import com.jc.proto.msg.LoginMsg;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
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
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.HashedWheelTimer;

public class MasterLiveBuilder {

	private static Logger logger = LoggerFactory.getLogger(MasterLiveBuilder.class);
	
	protected final HashedWheelTimer timer = new HashedWheelTimer();

	private Bootstrap boot;

	private final ClientIdleStateTrigger idleStateTrigger = new ClientIdleStateTrigger();
	
	private ServerMod cenjcs;
	
	private SocketChannel masterChannel;
	
	public MasterLiveBuilder(ServerMod centjcs) {
		this.cenjcs = centjcs;
	}
	
	public ServerMod info() {
		return cenjcs;
	}
	
	public String host() throws Exception {
		URI uri = new URI(this.cenjcs.getMaster());
		return uri.getHost();
	}
	
	public Integer port() throws Exception {
		URI uri = new URI(this.cenjcs.getMaster());
		Integer port = uri.getPort();
		if(port<=0) {
			throw new RuntimeException("please check port param");
		}
		return port;
	}

	public void connect() throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();

		boot = new Bootstrap();
		boot.group(group).channel(NioSocketChannel.class).handler(new LoggingHandler(LogLevel.INFO));
		boot.option(ChannelOption.SO_KEEPALIVE, true);
		
		final MasterOberver watchdog = new MasterOberver(boot, timer, port(), host(), true) {
			public ChannelHandler[] handlers() {
				return new ChannelHandler[] { 
						new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS), 
						idleStateTrigger, this, 
						new ObjectEncoder(), 
						new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
						new NettyClientHandler(), 
						new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 2) };
			}
		};
		watchdog.bindKeeper(this);
		ChannelFuture future;
		// 进行连接
		try {
			synchronized (boot) {
				boot.handler(new ChannelInitializer<Channel>() {
					// 初始化channel
					@Override
					protected void initChannel(Channel ch) throws Exception {
						ch.pipeline().addLast(watchdog.handlers());
					}
				});
				try {
					future = boot.connect(host(), port()).sync();
				} catch(Exception e) {
					future = null;
				}
				if(future==null||!future.isSuccess()) {
					int try_count = 0;
					for(;;) {
						TimeUnit.MILLISECONDS.sleep(5000L);
						try {
							future = boot.connect(host(), port()).sync();
							logger.info("connec againing");
							if(future.isSuccess()) {
								masterChannel = (SocketChannel)future.channel();
								LoginMsg loginMsg = new LoginMsg();
								loginMsg.setClientId(this.cenjcs.getId());
								masterChannel.writeAndFlush(loginMsg);
								break;
							}
						} catch(Exception e) {
							logger.error("try_count" + (++try_count));
						}
					}
				} else {
					//直接进行登陆校验
					masterChannel = (SocketChannel)future.channel();
					LoginMsg loginMsg = new LoginMsg();
					loginMsg.setClientId(this.cenjcs.getId());
					masterChannel.writeAndFlush(loginMsg);
				}
			}

			// 以下代码在synchronized同步块外面是安全的
			// future.sync();
		} catch (Throwable t) {
			throw new Exception("connects to  fails", t);
		}
	}

}
