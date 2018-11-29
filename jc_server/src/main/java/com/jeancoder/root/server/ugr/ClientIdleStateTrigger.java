package com.jeancoder.root.server.ugr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

@ChannelHandler.Sharable
public class ClientIdleStateTrigger extends ChannelInboundHandlerAdapter {

	private static Logger logger = LoggerFactory.getLogger(ClientIdleStateTrigger.class);

	private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("heartpopup", CharsetUtil.UTF_8));

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleState state = ((IdleStateEvent) evt).state();
			if (state == IdleState.WRITER_IDLE) {
				Channel current_channel = ctx.channel();
				ChannelId chid = current_channel.id();

				logger.error("channel_id=" + chid + " will be set IDLE STATE." + current_channel);
				//throw new Exception("idle exception");
				ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate());
			}
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}
}
