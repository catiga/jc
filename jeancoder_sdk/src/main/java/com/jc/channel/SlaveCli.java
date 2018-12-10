package com.jc.channel;

import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jc.proto.conf.AppMod;
import com.jc.proto.msg.GeneralMsg;
import com.jc.proto.msg.SyncMsg;
import com.jc.proto.msg.ct.InstallMsg;
import com.jc.proto.msg.ct.UninstallMsg;
import com.jc.proto.msg.ct.UpgradeMsg;
import com.jc.shell.JCShellFac;
import com.jc.shell.ShellServer;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.SocketChannel;

public class SlaveCli {
	
	private static Logger logger = LoggerFactory.getLogger(SlaveCli.class.getName());
	
	static final SlaveCli instance = new SlaveCli();
	
	public static SlaveCli instance() {
		return instance;
	}
	
	public Enumeration<ShellServer> localServers() {
		return JCShellFac.instance().servers();
	}
	
	public List<String> slaveServers() {
		return JCShellFac.instance().dispatchlist();
	}
	
	public void upgradeApp(String client_instance_id, AppMod am) {
		UpgradeMsg msg = new UpgradeMsg(am);
		
		SocketChannel channel = (SocketChannel)JCShellFac.instance().dispatchaim(client_instance_id);
		if(channel==null) {
			throw new RuntimeException(client_instance_id + " server not connected.");
		}
		channel.writeAndFlush(msg);
	}
	
	public void installApp(String client_instance_id, AppMod am) {
		InstallMsg msg = new InstallMsg(am);
		
		SocketChannel channel = (SocketChannel)JCShellFac.instance().dispatchaim(client_instance_id);
		if(channel==null) {
			throw new RuntimeException(client_instance_id + " server not connected.");
		}
		channel.writeAndFlush(msg);
	}
	
	public void uninstallApp(String client_instance_id, AppMod am) {
		UninstallMsg msg = new UninstallMsg(am);
		
		SocketChannel channel = (SocketChannel)JCShellFac.instance().dispatchaim(client_instance_id);
		if(channel==null) {
			throw new RuntimeException(client_instance_id + " server not connected.");
		}
		channel.writeAndFlush(msg);
	}
	
	public <M extends GeneralMsg, I extends SyncMsg> void fireSyncMsg(String client_instance_id, I sync_msg) {
		SocketChannel channel = (SocketChannel)JCShellFac.instance().dispatchaim(client_instance_id);
		if(channel==null) {
			throw new RuntimeException(client_instance_id + " server not connected.");
		}
		String sync_msg_id = sync_msg.getUnionid();
		ChannelFuture future = channel.writeAndFlush(sync_msg);
		future.addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture f) throws Exception {
				boolean succeed = f.isSuccess();
				if(succeed) {
					boolean add_result = JCShellFac.instance().emptyMsg(sync_msg_id);
					if(!add_result) {
						logger.error(sync_msg_id + " was not persisted successfully.");
					}
				}
			}
		});
	}
	
	public GeneralMsg consumeSyncMsg(String message_id) {
		Long default_time_out = 5000L;	//默认等待时间为5秒
		//return JCShellFac.instance().consumeMsg(message_id);
		return consumeSyncMsg(message_id, default_time_out);
	}
	
	public GeneralMsg consumeSyncMsg(String message_id, Long timeout) {
		Long timecounter = 0l;
		GeneralMsg ret_msg = null;
		for(;;) {
			timecounter += 500l;
			try {
				TimeUnit.MILLISECONDS.sleep(500l);
				ret_msg = JCShellFac.instance().consumeMsg(message_id);
				if(ret_msg!=null) {
					return ret_msg;
				}
			} catch (InterruptedException e) {
				logger.error("", e);
			} catch (Exception e) {
				logger.error("", e);
			}
			if(timecounter>timeout) {
				return ret_msg;
			}
		}
	}
}

