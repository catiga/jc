package com.jeancoder.root.server.proto.conf;

import java.util.List;

public class FkConf {

	Ins ins;
	
	List<ServerMod> servers;
	
	List<AppMod> apps;

	public Ins getIns() {
		return ins;
	}

	public void setIns(Ins ins) {
		this.ins = ins;
	}

	public List<ServerMod> getServers() {
		return servers;
	}

	public void setServers(List<ServerMod> servers) {
		this.servers = servers;
	}

	public List<AppMod> getApps() {
		return apps;
	}

	public void setApps(List<AppMod> apps) {
		this.apps = apps;
	}
	
}
