package com.jeancoder.root.server.ugr;

import java.io.InputStream;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jc.proto.conf.AppMod;
import com.jc.proto.msg.GeneralMsg;
import com.jc.proto.msg.LoginMsg;
import com.jc.proto.msg.MsgType;
import com.jc.proto.msg.ReplyClientBody;
import com.jc.proto.msg.ReplyMsg;
import com.jc.proto.msg.ReplyServerBody;
import com.jc.proto.msg.ct.InstallMsg;
import com.jc.proto.msg.ct.UninstallMsg;
import com.jc.proto.msg.ct.UpgradeMsg;
import com.jc.proto.msg.ct.VmContainerMsg;
import com.jc.proto.msg.qd.SelectHandler;
import com.jc.proto.msg.qd.TablesHandler;
import com.jeancoder.root.container.ContainerMaps;
import com.jeancoder.root.server.util.RemoteUtil;
import com.jeancoder.root.server.util.ZipUtil;
import com.jeancoder.root.vm.JCVM;
import com.jeancoder.root.vm.JCVMDelegatorGroup;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

public class NettyClientHandler extends SimpleChannelInboundHandler<GeneralMsg> {
	
	private static Logger logger = LoggerFactory.getLogger(NettyClientHandler.class.getName());
	
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
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						InputStream ins = RemoteUtil.installation(appinfo.getFetch_address(),new Long(appinfo.getApp_id()));
						ZipUtil.unzip(appinfo.getApp_base(), new ZipInputStream(ins));
						JCVM jcvm = JCVMDelegatorGroup.instance().getDelegator().getVM();
						jcvm.updateApp(appinfo.to());
					} catch (Exception e) {
						logger.error("", e);
					}
				}
			}).start();
		}
			break;
		case APPINSTALL: {
			InstallMsg unmsg = (InstallMsg)baseMsg;
			AppMod appinfo = unmsg.getAppins();
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						InputStream ins = RemoteUtil.installation(appinfo.getFetch_address(),new Long(appinfo.getApp_id()));
						ZipUtil.unzip(appinfo.getApp_base(), new ZipInputStream(ins));
						JCVM jcvm = JCVMDelegatorGroup.instance().getDelegator().getVM();
						jcvm.installApp(appinfo.to());
					} catch (Exception e) {
						logger.error("", e);
					}
				}
			}).start();

		}
			break;
		case APPUNINSTALL: {
			UninstallMsg unmsg = (UninstallMsg)baseMsg;
			AppMod appinfo = unmsg.getAppins();
			JCVM jcvm = JCVMDelegatorGroup.instance().getDelegator().getVM();
			jcvm.uninstallApp(appinfo.to());
		}
			break;
		case APPCONTAINERS: {
			JCVM jcvm = JCVMDelegatorGroup.instance().getDelegator().getVM();
			ContainerMaps conts = jcvm.getContainers();
			VmContainerMsg reply = new VmContainerMsg(conts);
			channelHandlerContext.writeAndFlush(reply);
		}
			break;
			
		case HANDLER_SELECT: {
			SelectHandler select = (SelectHandler)baseMsg;
			logger.info(select.toString()); 	// TODO 
		}
			break;
		
		case HANDLER_TABLES: {
			TablesHandler msg = (TablesHandler)baseMsg;
			System.out.println(msg);
		}
			break;
		default:
			break;
		}
		ReferenceCountUtil.release(msgType);
	}
}
