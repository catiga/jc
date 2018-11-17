package com.jeancoder.root.server.proto.conf;

import java.util.List;

public class FkConf {

	Ins ins;
	
	List<ServerMod> servers;
	
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

}
