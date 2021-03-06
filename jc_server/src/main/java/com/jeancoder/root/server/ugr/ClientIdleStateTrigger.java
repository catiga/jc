package com.jeancoder.root.server.ugr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jc.proto.msg.Constants;
import com.jc.proto.msg.PingMsg;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

@ChannelHandler.Sharable
public class ClientIdleStateTrigger extends ChannelInboundHandlerAdapter {

	private static Logger logger = LoggerFactory.getLogger("C_IDLESTATE_HANDLER");

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		Channel current_channel = ctx.channel();
		ChannelId chid = current_channel.id();
		String client_id = Constants.getClientId();
		if (evt instanceof IdleStateEvent) {
			IdleState state = ((IdleStateEvent) evt).state();
			if(logger.isDebugEnabled()) {
				logger.debug("IDST TYPE:::" + state + ",CHANNEL_ID=" + chid + "[" + client_id + "]" + " BESET IDST." + ", CHOPUCSE:::" + Constants.getClientId());
			}
			switch (state) {
			case READER_IDLE:
				handleReaderIdle(ctx);
				break;
			case WRITER_IDLE:
				handleWriterIdle(ctx);
				break;
			case ALL_IDLE:
				handleAllIdle(ctx);
				break;
			default:
				break;
			}

			// if (state == IdleState.WRITER_IDLE) {
			// logger.info("channel_id=" + chid + " will be set IDLE STATE." +
			// ", and hbid=" + Constants.getClientId() + ", chopucse:::" +
			// Constants.getClientId());
			// PingMsg pingMsg = new PingMsg();
			// ctx.writeAndFlush(pingMsg);
			// }
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}

	protected void handleReaderIdle(ChannelHandlerContext ctx) {
		if(logger.isDebugEnabled()) {
			logger.debug("---READER_IDLE---" + ctx.channel());
		}
		PingMsg pingMsg = new PingMsg();
		ctx.channel().writeAndFlush(pingMsg);
		if(logger.isDebugEnabled()) {
			logger.debug("pingmg over:::" + ctx.channel());
		}
	}

	protected void handleWriterIdle(ChannelHandlerContext ctx) {
		if(logger.isDebugEnabled()) {
			logger.debug("---WRITER_IDLE---" + ctx.channel());
		}
		PingMsg pingMsg = new PingMsg();
		ctx.channel().writeAndFlush(pingMsg);
		if(logger.isDebugEnabled()) {
			logger.debug("pingmg over:::" + ctx.channel());
		}
	}

	protected void handleAllIdle(ChannelHandlerContext ctx) {
		if(logger.isDebugEnabled()) {
			logger.debug("---ALL_IDLE---" + ctx.channel());
		}
		PingMsg pingMsg = new PingMsg();
		ctx.channel().writeAndFlush(pingMsg);
		if(logger.isDebugEnabled()) {
			logger.debug("pingmg over:::" + ctx.channel());
		}
	}

}
