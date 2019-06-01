package com.jeancoder.root.server.ugr;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jc.proto.msg.LoginMsg;
import com.jeancoder.root.server.channels.ChannelHandlerHolder;
import com.jeancoder.root.server.util.LoginConnect;

/**
 * Created by Administrator on 2016/9/22.
 */
/**
 *
 * 重连检测狗，当发现当前的链路不稳定关闭之后，进行12次重连
 */
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

@Sharable
public abstract class MasterObserver extends ChannelInboundHandlerAdapter implements TimerTask, ChannelHandlerHolder {

	private static Logger logger = LoggerFactory.getLogger(MasterObserver.class.getName());

	private final Bootstrap bootstrap;
	private final Timer timer;
	private final int port;
	private final String host;
	
	private MasterLiveBuilder keeper;
	
	private SocketChannel masterChannel;
	private volatile boolean reconnect = true;
	private int attempts;

	public MasterObserver(Bootstrap bootstrap, Timer timer, int port, String host, boolean reconnect) {
		this.bootstrap = bootstrap;
		this.timer = timer;
		this.port = port;
		this.host = host;
		this.reconnect = reconnect;
	}
	
	public void bindKeeper(MasterLiveBuilder keeper) {
		this.keeper = keeper;
	}
	/**
	 * channel链路每次active的时候，将其连接的次数重新☞ 0
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("master channel actived, and reset the reconn counters t0");
		attempts = 0;
		ctx.fireChannelActive();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info("connection closed");
		if (reconnect) {
			logger.info("connection will be reset");
			if (attempts<=30) {
				attempts++;
			} // 重连的间隔时间会越来越长
			long timeout = 2 << attempts;
			timeout = 5000L;
			//timer.newTimeout(this, timeout, TimeUnit.MILLISECONDS);
			timer.newTimeout(this, timeout, TimeUnit.MILLISECONDS);
		}
		ctx.fireChannelInactive();
	}

	public void run(Timeout timeout) throws Exception {
		ChannelFuture future;
		// bootstrap已经初始化好了，只需要将handler填入就可以了
		synchronized (bootstrap) {
			bootstrap.handler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline().addLast(handlers());
				}
			});
			future = bootstrap.connect(host, port);
		}
		// future对象
		future.addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture f) throws Exception {
				boolean succeed = f.isSuccess();
				// 如果重连失败，则调用ChannelInactive方法，再次出发重连事件，一直尝试12次，如果失败则不再重连
				if (!succeed) {
					logger.info("reconnect failure......");
					f.channel().pipeline().fireChannelInactive();
				} else {
					masterChannel = (SocketChannel)future.channel();
					//LoginMsg loginMsg = new LoginMsg();
					LoginMsg loginMsg = LoginConnect.buildLoginMsg();
					String client_id = keeper.info().getId();
					loginMsg.setClientId(client_id);
					masterChannel.writeAndFlush(loginMsg);
					logger.info("reconnect success");
				}
			}
		});
	}

}
