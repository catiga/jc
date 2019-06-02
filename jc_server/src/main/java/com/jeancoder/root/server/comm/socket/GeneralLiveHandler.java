package com.jeancoder.root.server.comm.socket;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jc.proto.msg.AskMsg;
import com.jc.proto.msg.GeneralMsg;
import com.jc.proto.msg.LoginMsg;
import com.jc.proto.msg.MsgProto;
import com.jc.proto.msg.MsgType;
import com.jc.proto.msg.PingMsg;
import com.jc.proto.msg.ReplyClientBody;
import com.jc.proto.msg.ReplyMsg;
import com.jc.proto.msg.ReplyServerBody;
import com.jeancoder.core.power.DatabasePower;
import com.jeancoder.core.power.result.JeancoderResultSet;
import com.jeancoder.root.container.ContainerMaps;
import com.jeancoder.root.container.JCAppContainer;
import com.jeancoder.root.server.state.NettyChannelMap;
import com.jeancoder.root.server.state.ServerHolder;
import com.jeancoder.root.vm.JCVM;
import com.jeancoder.root.vm.JCVMDelegatorGroup;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.ReferenceCountUtil;

public class GeneralLiveHandler extends SimpleChannelInboundHandler<MsgProto> {

	private static Logger logger = LoggerFactory.getLogger(GeneralLiveHandler.class.getName());
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info(ctx.channel().localAddress().toString() + " 通道不活跃！" + ctx.channel().isActive());
		NettyChannelMap.remove((SocketChannel) ctx.channel());
	}
	
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info(ctx.channel().localAddress().toString() + " 通道已激活！");
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		//logger.info("服务端接收数据完毕..");
		
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
		logger.error("exception:" + cause.getMessage(), cause);
		//ctx.close();
	}
	
	@SuppressWarnings("deprecation")
	protected List<Map<String, String>> findInstanceByMerchantAndInstanceCode(String merchant_code, String instance_code) {
		JCVM jcvm = JCVMDelegatorGroup.instance().getDelegator().getVM();
		ContainerMaps conts = jcvm.getContainers();
		JCAppContainer container = conts.getByCode("server").nextElement();		//这里就是找中央服务的code对应的容器
		
		try {
			DatabasePower db_pow = container.getCaps().getDatabase();
			String sql = "select id, name, ver_install_id, now_ver_id, now_ver_num from j_instance where code='" + instance_code + "' and m_id in (select id from j_merchants where merchants_code='" + merchant_code + "')";
			JeancoderResultSet result = null;
			try {
				result = db_pow.doQuery(sql);
				ResultSet rs = result.getResultSet();
				List<Map<String, String>> tables = new LinkedList<Map<String, String>>();
				while(rs.next()) {
					Map<String, String> data_i = new HashMap<String, String>();
					ResultSetMetaData metadata = rs.getMetaData();
					int col_size = metadata.getColumnCount();
					for(int i=1; i<=col_size; i++) {
						String column_name = metadata.getColumnName(i);
						String column_alias_name = metadata.getColumnLabel(i);
						if(column_alias_name!=null) {
							column_name = column_alias_name;
						}
						Object value = rs.getObject(i);
						data_i.put(column_name, value==null?null:value.toString());
					}
					tables.add(data_i);
				}
				return tables;
			} catch(Exception e) {
				throw new RuntimeException(e);
			} finally {
				if(result!=null) {
					result.closeConnection();
				}
			}
		} catch(Exception e) {
			logger.error("", e);
			return null;
		}
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, MsgProto msgProto) throws Exception {
		String version = msgProto.version();		//消息版本号
		String digest = msgProto.digest();			//根据消息摘要判断是哪个版本的消息
		String msg_id = msgProto.getUnionid();
		String instance_id = msgProto.getClientId();
		//这两个数据留待下一个版本的容器升级使用
		logger.debug("client_id:" + instance_id + ", msg_id=" + msg_id + ", version=" + version + ", digest=" + digest);
		
		GeneralMsg baseMsg = (GeneralMsg)msgProto;
		logger.debug("server receive message=" + baseMsg + " and from client_id:::" + baseMsg.getClientId());
		if (MsgType.LOGIN.equals(baseMsg.getType())) {
			LoginMsg loginMsg = (LoginMsg) baseMsg;
			String user_name = loginMsg.getUserName();	//这里应该是 merchant_code
			String password = loginMsg.getPassword();	//这里应该是 instance_code
			
			List<Map<String, String>> instance_data = this.findInstanceByMerchantAndInstanceCode(user_name, password);
			SocketChannel need_save_channel = null;
			if(instance_data!=null) {
				if(instance_data.size()>1) {
					//说明配置错误，需要打印日志并提醒
					logger.error("LOGIN_CONFIG_ERROR:client_id:" + instance_id + " and merchant_code=" + user_name + " and instance_code=" + password + " was repeated, please check...");
				} else {
					Map<String, String> instance_obj = instance_data.get(0);
					String instance_obj_id = instance_obj.get("id").toString();			// 一定不是null
					String instance_obj_name = instance_obj.get("name")==null ? null : instance_obj.get("name").toString();
					String instance_obj_ver_install_id = instance_obj.get("ver_install_id")==null ? null : instance_obj.get("ver_install_id").toString();
					String instance_obj_now_ver_id = instance_obj.get("now_ver_id")==null ? null : instance_obj.get("now_ver_id").toString();
					String instance_obj_now_ver_num = instance_obj.get("now_ver_num")==null ? null : instance_obj.get("now_ver_num").toString();
					
					logger.info("LOGIN MSG>>>");
					logger.info("instance_obj_id=" + instance_obj_id + ", instance_obj_name=" + instance_obj_name 
							+ ", instance_obj_ver_install_id=" + instance_obj_ver_install_id
							+ ", instance_obj_now_ver_id=" + instance_obj_now_ver_id
							+ ", instance_obj_now_ver_num=" + instance_obj_now_ver_num);
					if(instance_obj_id.equals(instance_id)) {
						//成功匹配到
						need_save_channel = (SocketChannel)ctx.channel();
					} else {
						logger.error("instance_id=" + instance_obj_id + " login failed and invalid request from IP:" + ctx.channel().remoteAddress());
					}
				}
			} else {
				logger.error("merchant_code=" + user_name + " and instance_code=" + password + " was not found, try the old version code.");
				//旧的连接方式
				if ("jclogin".equals(user_name) && "jcpass".equals(password)) {	//暂存的处理方式
					// 登录成功,把channel存到服务端的map中
//					NettyChannelMap.add(loginMsg.getClientId(), (SocketChannel) ctx.channel());
//					logger.info("client" + loginMsg.getClientId() + " 登录成功");
					
					//成功匹配到旧版本
					need_save_channel = (SocketChannel)ctx.channel();
				} else {
					//判断一下是不是从腾讯视频过来的连接
					if(instance_id.equals("7") || instance_id.equals("8") || instance_id.equals("2")) {
						need_save_channel = (SocketChannel)ctx.channel();
					}
				}
			}
			if(need_save_channel!=null) {
				NettyChannelMap.add(loginMsg.getClientId(), need_save_channel);
				logger.info("client" + loginMsg.getClientId() + " 登录成功");
			} else {
				ctx.channel().close();
			}
		} else if (MsgType.PING.equals(baseMsg.getType())) {
			PingMsg pingMsg = (PingMsg)baseMsg;
			if(pingMsg.getClientId()!=null) {
				//execute update channel 
				String channel_client_id = pingMsg.getClientId();
				Channel exist_channel = NettyChannelMap.get(channel_client_id);
				if(exist_channel!=null) {
					if(exist_channel!=ctx.channel()) {
						logger.info("channel_client_id:::" + channel_client_id + " will be updated for The exist channel:::" + exist_channel + " is not equle the new one:::" + ctx.channel());
					}
					NettyChannelMap.add(pingMsg.getClientId(), (SocketChannel) ctx.channel());
					PingMsg replyPing = new PingMsg();
					NettyChannelMap.get(pingMsg.getClientId()).writeAndFlush(replyPing);
				} else {
					logger.info("channel_client_id:::" + channel_client_id + " does not exist, invalid connect will be dropped");
					ctx.channel().close();
				}
//				if(exist_channel==null || (exist_channel!=ctx.channel())) {
//					logger.info("channel_client_id:::" + channel_client_id + " will be updated for The exist channel:::" + exist_channel + " is not equle the new one:::" + ctx.channel());
//				}
//				NettyChannelMap.add(pingMsg.getClientId(), (SocketChannel) ctx.channel());
//				PingMsg replyPing = new PingMsg();
//				NettyChannelMap.get(pingMsg.getClientId()).writeAndFlush(replyPing);
			}
		} else {
//			if (NettyChannelMap.get(baseMsg.getClientId()) == null) {
				// 说明未登录，或者连接断了，服务器向客户端发起登录请求，让客户端重新登录
//				LoginMsg loginMsg = new LoginMsg();
//				ctx.channel().writeAndFlush(loginMsg);
//			}
			
			
			switch (baseMsg.getType()) {
//			case PING: {
//				PingMsg pingMsg = (PingMsg) baseMsg;
//				PingMsg replyPing = new PingMsg();
//				NettyChannelMap.get(pingMsg.getClientId()).writeAndFlush(replyPing);
//			}
//				break;
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
				logger.info("THE DEFAULT MSG HANDLER INPUT:" + baseMsg);
				String message_id = baseMsg.getUnionid();
				String client_id = baseMsg.getClientId();
				MsgType message_type = baseMsg.getType();
				if(message_id==null) {
					logger.error("CLIENT_ID=" + client_id + "; MESSAGE_ID=" + message_id + "; MSG_TYPE=" + message_type + " will be dicarded, for the message id empty");
				} else {
					this.disposeSyncOrExchangeMsg(baseMsg);
				}
				break;
			}
		}

		ReferenceCountUtil.release(baseMsg);
	}
	
	protected <T extends GeneralMsg> void disposeSyncOrExchangeMsg(T msg) {
		String msg_id = msg.getUnionid();
		ServerHolder.getHolder().syncMsg(msg_id, msg);
	}
}
