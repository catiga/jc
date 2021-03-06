package com.jeancoder.root.server.comm.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeancoder.root.env.ChannelContextWrapper;
import com.jeancoder.root.manager.JCVMDelegator;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class JCEnvHandler extends ChannelInboundHandlerAdapter {

	private static Logger logger = LoggerFactory.getLogger(JCEnvHandler.class);
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		JCVMDelegator.bindContext(ChannelContextWrapper.newone(ctx));
		if(logger.isDebugEnabled()) {
			logger.debug(ctx.channel().id().toString() + " is actived");
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.channelInactive(ctx);
		if(logger.isDebugEnabled()) {
			logger.debug(ctx.channel().id().toString() + " is closed");
		}
		//ctx.channel().close();
	}
	
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
    	logger.error(e.getMessage(),e);
    	ctx.channel().close();
    }
	
}
