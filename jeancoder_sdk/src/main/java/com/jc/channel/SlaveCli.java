package com.jc.channel;

import java.util.Enumeration;

import com.jc.proto.conf.AppMod;
import com.jc.proto.msg.ct.UpgradeMsg;
import com.jc.shell.JCShellFac;
import com.jc.shell.ShellServer;

import io.netty.channel.socket.SocketChannel;

public class SlaveCli {
	
	static final SlaveCli instance = new SlaveCli();
	
	public static SlaveCli instance() {
		return instance;
	}
	
	public Enumeration<ShellServer> servers() {
		return JCShellFac.instance().servers();
	}
	
	public void upgradeApp(String client_instance_id, AppMod am) {
		UpgradeMsg msg = new UpgradeMsg(am);
		
		SocketChannel channel = (SocketChannel)JCShellFac.instance().dispatchaim(client_instance_id);
		channel.writeAndFlush(msg);
	}
}
