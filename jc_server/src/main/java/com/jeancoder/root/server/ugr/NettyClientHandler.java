package com.jeancoder.root.server.ugr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeancoder.root.server.proto.conf.AppMod;
import com.jeancoder.root.server.proto.msg.GeneralMsg;
import com.jeancoder.root.server.proto.msg.LoginMsg;
import com.jeancoder.root.server.proto.msg.MsgType;
import com.jeancoder.root.server.proto.msg.ReplyClientBody;
import com.jeancoder.root.server.proto.msg.ReplyMsg;
import com.jeancoder.root.server.proto.msg.ReplyServerBody;
import com.jeancoder.root.server.proto.msg.ct.UninstallMsg;
import com.jeancoder.root.server.proto.msg.ct.UpgradeMsg;
import com.jeancoder.root.vm.JCVM;
import com.jeancoder.root.vm.JCVMDelegatorGroup;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

public class NettyClientHandler extends SimpleChannelInboundHandler<GeneralMsg> {
	
	private static Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);
	
	// @Override
	// public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
	// throws Exception {
	// if (evt instanceof IdleStateEvent) {
	// IdleStateEvent e = (IdleStateEvent) evt;
	// switch (e.state()) {
	// case WRITER_IDLE:
	// PingMsg pingMsg=new PingMsg();
	// ctx.writeAndFlush(pingMsg);
	// System.out.println("send ping to server----------");
	// break;
	// default:
	// break;
	// }
	// }
	// }

	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, GeneralMsg baseMsg) throws Exception {
		MsgType msgType = baseMsg.getType();
		switch (msgType) {
		case LOGIN: {
			// 向服务器发起登录
			LoginMsg loginMsg = new LoginMsg();
			channelHandlerContext.writeAndFlush(loginMsg);
		}
			break;
		case PING: {
			logger.debug("receive ping from server----------");
		}
			break;
		case ASK: {
			ReplyClientBody replyClientBody = new ReplyClientBody("client info **** !!!");
			ReplyMsg replyMsg = new ReplyMsg();
			replyMsg.setBody(replyClientBody);
			channelHandlerContext.writeAndFlush(replyMsg);
		}
			break;
		case REPLY: {
			ReplyMsg replyMsg = (ReplyMsg) baseMsg;
			ReplyServerBody replyServerBody = (ReplyServerBody) replyMsg.getBody();
			logger.debug("receive server msg: " + replyServerBody.getServerInfo());
		}
			break;
		case APPUPGRADE: {
			UpgradeMsg unmsg = (UpgradeMsg)baseMsg;
			AppMod appinfo = unmsg.getAppins();
			JCVM jcvm = JCVMDelegatorGroup.instance().getDelegator().getVM();
			jcvm.updateApp(appinfo.to());
		}
			break;
		default:
			break;
		}
		ReferenceCountUtil.release(msgType);
	}
}
