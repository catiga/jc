package com.jeancoder.root.server.ugr;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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
import com.jc.proto.msg.SyncMsg;
import com.jc.proto.msg.ct.InstallMsg;
import com.jc.proto.msg.ct.UninstallMsg;
import com.jc.proto.msg.ct.UpgradeMsg;
import com.jc.proto.msg.ct.VmContainerMsg;
import com.jc.proto.msg.monit.ReqHandler;
import com.jc.proto.msg.paramdebug.ParamHandler;
import com.jc.proto.msg.paramdebug.ParamMod;
import com.jc.proto.msg.qd.DataHandler;
import com.jc.proto.msg.qd.SelectHandler;
import com.jc.proto.msg.qd.TablesHandler;
import com.jeancoder.core.power.DatabasePower;
import com.jeancoder.core.power.result.JeancoderResultSet;
import com.jeancoder.core.util.JackSonBeanMapper;
import com.jeancoder.root.container.ContainerMaps;
import com.jeancoder.root.container.JCAppContainer;
import com.jeancoder.root.server.state.GlobalStateHolder;
import com.jeancoder.root.server.state.RequestStateHolder;
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
	
	protected void fireGeneralMsg(ChannelHandlerContext ctx, GeneralMsg revMsg, GeneralMsg sendMsg) {
		if(revMsg instanceof SyncMsg) {
			sendMsg.resetUnionid(revMsg.getUnionid());
		}
		ctx.writeAndFlush(sendMsg);
	}

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
					JCVM jcvm = JCVMDelegatorGroup.instance().getDelegator().getVM();
					try {
						InputStream ins = RemoteUtil.installation(appinfo.getFetch_address(),new Long(appinfo.getApp_id()));
						jcvm.uninstallApp(appinfo.to());
						ZipUtil.unzip(appinfo.getApp_base(), new ZipInputStream(ins));
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
					JCVM jcvm = JCVMDelegatorGroup.instance().getDelegator().getVM();
					try {
						InputStream ins = RemoteUtil.installation(appinfo.getFetch_address(),new Long(appinfo.getApp_id()));
						jcvm.uninstallApp(appinfo.to());
						ZipUtil.unzip(appinfo.getApp_base(), new ZipInputStream(ins));
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
			fireGeneralMsg(channelHandlerContext, baseMsg, reply);
		}
			break;
			
		case HANDLER_SELECT: {
			SelectHandler msg = (SelectHandler)baseMsg;
			Object data = this.extractData(msg);
			msg.setData(data);
			fireGeneralMsg(channelHandlerContext, msg, msg);
		}
			break;
		
		case HANDLER_TABLES: {
			TablesHandler msg = (TablesHandler)baseMsg;
			Object data = this.extractData(msg);
			msg.setData(data);
			fireGeneralMsg(channelHandlerContext, msg, msg);
		}
			break;
		
		case MONIT_REQ: {
			ReqHandler msg = (ReqHandler)baseMsg;
			Object data = RequestStateHolder.INSTANCE.trigger();
			msg.setData(data);
			fireGeneralMsg(channelHandlerContext, msg, msg);
		}
			break;
			
		case INSPARAD: {
			logger.info("accepted params settings:" + JackSonBeanMapper.toJson(baseMsg));
			if(baseMsg!=null && (baseMsg instanceof ParamHandler)) {
				ParamHandler msg = (ParamHandler)baseMsg;
				ParamMod mod = msg.getParams();
				GlobalStateHolder.INSTANCE.reset(mod);
				msg.success();
				fireGeneralMsg(channelHandlerContext, msg, msg);
			} else {
				logger.error("msg type not match:" + MsgType.INSPARAD.name());
			}
		}
			break;
		default:
			break;
		}
		ReferenceCountUtil.release(msgType);
	}
	
	protected Object extractData(DataHandler datahandler) {
		JCVM jcvm = JCVMDelegatorGroup.instance().getDelegator().getVM();
		ContainerMaps conts = jcvm.getContainers();
		JCAppContainer container = conts.getByCode(datahandler.getContcode()).nextElement();
		
		try {
			DatabasePower db_pow = container.getCaps().getDatabase();
			Object ret_data = null;
			if(datahandler instanceof TablesHandler) {
				ret_data = this.extractDbTables(db_pow, (TablesHandler)datahandler);
			} else if(datahandler instanceof SelectHandler) {
				ret_data = this.extractDbTables(db_pow, (SelectHandler)datahandler);
			}
			return ret_data;
		} catch(Exception e) {
			logger.error("", e);
			return null;
		}
	}
	
	@SuppressWarnings("deprecation")
	private List<String> extractDbTables(DatabasePower db_pow, TablesHandler datahandler) {
		JeancoderResultSet result = null;
		try {
			result = db_pow.doQuery(datahandler.getSql());
			ResultSet rs = result.getResultSet();
			List<String> tables = new LinkedList<>();
			while(rs.next()) {
				tables.add(rs.getString(1));
			}
			return tables;
		} catch(Exception e) {
			throw new RuntimeException(e);
		} finally {
			if(result!=null) {
				result.closeConnection();
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private List<String[]> extractDbTables(DatabasePower db_pow, SelectHandler datahandler) {
		JeancoderResultSet result = null;
		
		List<String[]> tables = new LinkedList<String[]>();
		
		try {
			result = db_pow.doQuery(datahandler.getSql());
			ResultSet rs = result.getResultSet();
			
			ResultSetMetaData metadata = rs.getMetaData();
			int col_size = metadata.getColumnCount();
			List<String> heads = new ArrayList<>(col_size);
			for(int i=1; i<=col_size; i++) {
				String column_name = metadata.getColumnName(i);
				String column_alias_name = metadata.getColumnLabel(i);
				if(column_alias_name!=null) {
					column_name = column_alias_name;
				}
				heads.add(column_name);
			}
			tables.add(heads.toArray(new String[col_size]));
			while(rs.next()) {
				List<String> data = new ArrayList<>(col_size);
				for(int i=1; i<=col_size; i++) {
					data.add(rs.getString(i));
				}
				tables.add(data.toArray(new String[col_size]));
			}
			return tables;
		} catch(Exception e) {
			throw new RuntimeException(e);
		} finally {
			if(result!=null) {
				result.closeConnection();
			}
		}
	}
}
