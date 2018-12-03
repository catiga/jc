package com.jc;

import com.jc.proto.conf.ServerMod;

public class VmPsp {

	String proxyEntry;
	
	String proxyPath;
	
	String logBase;
	
	String master;
	
	String hostOnlyDisp;

	public String getProxyEntry() {
		return proxyEntry;
	}

	public void setProxyEntry(String proxyEntry) {
		this.proxyEntry = proxyEntry;
	}

	public String getProxyPath() {
		return proxyPath;
	}

	public void setProxyPath(String proxyPath) {
		this.proxyPath = proxyPath;
	}

	public String getLogBase() {
		return logBase;
	}

	public void setLogBase(String logBase) {
		this.logBase = logBase;
	}

	public String getMaster() {
		return master;
	}

	public void setMaster(String master) {
		this.master = master;
	}

	public String getHostOnlyDisp() {
		return hostOnlyDisp;
	}

	public void setHostOnlyDisp(String hostOnlyDisp) {
		this.hostOnlyDisp = hostOnlyDisp;
	}
	
	public static VmPsp build(ServerMod ser) {
		VmPsp t = new VmPsp();
		t.hostOnlyDisp = ser.getDomain_visit();
		t.logBase = ser.getLogs();
		t.master = ser.getMaster();
		t.proxyEntry = ser.getProxy_entry();
		t.proxyPath = ser.getProxy_path();
		return t;
	}
}
