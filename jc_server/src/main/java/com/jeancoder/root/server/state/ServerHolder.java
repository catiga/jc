package com.jeancoder.root.server.state;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import com.jc.proto.msg.GeneralMsg;
import com.jc.shell.JCShellFac;
import com.jc.shell.ShellChannelHolder;
import com.jeancoder.root.server.inet.JCServer;

import io.netty.channel.Channel;

public class ServerHolder implements ShellChannelHolder {

	private static final ServerHolder instance = new ServerHolder();
	
	private final static List<JCServer> iservers = new ArrayList<JCServer>();
	
	public static final ServerHolder getHolder() {
		return instance;
	}
	
	private ServerHolder() {
		JCShellFac.init(this);
	}
	
	public GeneralMsg consumeMsg(String message_id) {
		return TotalMessageConnector.consumeMsg(message_id);
	}
	
	public boolean emptyMsg(String message_id) {
		return TotalMessageConnector.addMsg(message_id);
	}
	
	public void syncMsg(String message_id, GeneralMsg message) {
		TotalMessageConnector.syncMsg(message_id, message);
	}
	
	public List<String> dispatchlist() {
		Enumeration<String> all_client_servers = NettyChannelMap.clients();
		List<String> all_servers = new ArrayList<>();
		while(all_client_servers.hasMoreElements()) {
			all_servers.add(all_client_servers.nextElement());
		}
		return all_servers;
	}
	
	public Channel dispatchaim(String id) {
		return NettyChannelMap.get(id);
	}
	
	@SuppressWarnings("unchecked")
	public Enumeration<JCServer> servers() {
		Vector<JCServer> sers = new Vector<>();
		if(!iservers.isEmpty()) {
			for(JCServer jcs : iservers) {
				sers.add(jcs);
			}
		}
		return sers.elements();
	}
	
	public void add(JCServer ser) {
		if(!ser.info().cocheck()) {
			throw new RuntimeException("server id empty");
		}
		boolean allowadd = true;
		for(JCServer ins : iservers) {
			if(ins.serverId().equals(ser.serverId())&&ins.defServerCode().equals(ser.defServerCode())) {
				allowadd = false;
				break;
			}
		}
		if(allowadd) {
			iservers.add(ser);
			return;
		}
		throw new RuntimeException();
	}
	
}
