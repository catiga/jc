package com.jeancoder.root.server.comm.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeancoder.root.server.proto.msg.AskMsg;
import com.jeancoder.root.server.proto.msg.GeneralMsg;
import com.jeancoder.root.server.proto.msg.LoginMsg;
import com.jeancoder.root.server.proto.msg.MsgType;
import com.jeancoder.root.server.proto.msg.PingMsg;
import com.jeancoder.root.server.proto.msg.ReplyClientBody;
import com.jeancoder.root.server.proto.msg.ReplyMsg;
import com.jeancoder.root.server.proto.msg.ReplyServerBody;
import com.jeancoder.root.server.state.NettyChannelMap;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.ReferenceCountUtil;

public class GeneralLiveHandler extends SimpleChannelInboundHandler<GeneralMsg> {

	private static Logger logger = LoggerFactory.getLogger(GeneralLiveHandler.class);
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info(ctx.channel().localAddress().toString() + " 通道不活跃！");
		NettyChannelMap.remove((SocketChannel) ctx.channel());
	}
	
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info(ctx.channel().localAddress().toString() + " 通道已激活！");
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		logger.info("服务端接收数据完毕..");
		//需要关闭的时候按照如下方法处理
		
		// 第一种方法：写一个空的buf，并刷新写出区域。完成后关闭sock channel连接。
		//ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
		// ctx.flush(); //
		// 第二种方法：在client端关闭channel连接，这样的话，会触发两次channelReadComplete方法。
		// ctx.flush().close().sync(); 
		// 第三种：改成这种写法也可以，但是这中写法，没有第一种方法的好。
	}
	
	/**
	 * 功能：服务端发生异常的操作
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("异常信息：\r\n" + cause.getMessage());
		ctx.close();
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, GeneralMsg baseMsg) throws Exception {
		logger.info("server receive message=" + baseMsg);
		if (MsgType.LOGIN.equals(baseMsg.getType())) {
			LoginMsg loginMsg = (LoginMsg) baseMsg;
			if ("robin".equals(loginMsg.getUserName()) && "yao".equals(loginMsg.getPassword())) {
				// 登录成功,把channel存到服务端的map中
				NettyChannelMap.add(loginMsg.getClientId(), (SocketChannel) ctx.channel());
				logger.info("client" + loginMsg.getClientId() + " 登录成功");
			}
		} else {
			if (NettyChannelMap.get(baseMsg.getClientId()) == null) {
				// 说明未登录，或者连接断了，服务器向客户端发起登录请求，让客户端重新登录
				LoginMsg loginMsg = new LoginMsg();
				ctx.channel().writeAndFlush(loginMsg);
			}
		}
		switch (baseMsg.getType()) {
		case PING: {
			PingMsg pingMsg = (PingMsg) baseMsg;
			PingMsg replyPing = new PingMsg();
			NettyChannelMap.get(pingMsg.getClientId()).writeAndFlush(replyPing);
		}
			break;
		case ASK: {
			// 收到客户端的请求
			AskMsg askMsg = (AskMsg) baseMsg;
			if ("authToken".equals(askMsg.getParams().getAuth())) {
				ReplyServerBody replyBody = new ReplyServerBody("server info $$$$ !!!");
				ReplyMsg replyMsg = new ReplyMsg();
				replyMsg.setBody(replyBody);
				NettyChannelMap.get(askMsg.getClientId()).writeAndFlush(replyMsg);
			}
		}
			break;
		case REPLY: {
			// 收到客户端回复
			ReplyMsg replyMsg = (ReplyMsg) baseMsg;
			ReplyClientBody clientBody = (ReplyClientBody) replyMsg.getBody();
			logger.info("receive client msg: " + clientBody.getClientInfo());
		}
			break;
		default:
			break;
		}
		ReferenceCountUtil.release(baseMsg);
	}

}
