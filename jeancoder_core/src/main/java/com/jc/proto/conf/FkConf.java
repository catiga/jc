package com.jc.proto.conf;

import java.util.List;

public class FkConf {

	Ins ins;
	
	InsPerf ins_perf;
	
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

	public InsPerf getIns_perf() {
		return ins_perf;
	}

	public void setIns_perf(InsPerf ins_perf) {
		this.ins_perf = ins_perf;
	}

}
