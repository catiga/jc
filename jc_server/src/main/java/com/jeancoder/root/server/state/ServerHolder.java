package com.jeancoder.root.server.state;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import com.jeancoder.root.server.inet.JCServer;

public class ServerHolder {

	private static final ServerHolder instance = new ServerHolder();
	
	private final static List<JCServer> iservers = new ArrayList<JCServer>();
	
	public static final ServerHolder getHolder() {
		return instance;
	}
	
	public List<String> dispatchlist() {
		Enumeration<String> all_client_servers = NettyChannelMap.clients();
		List<String> all_servers = new ArrayList<>();
		while(all_client_servers.hasMoreElements()) {
			all_servers.add(all_client_servers.nextElement());
		}
		return all_servers;
	}
	
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
