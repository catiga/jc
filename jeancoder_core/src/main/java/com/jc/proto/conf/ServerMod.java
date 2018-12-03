package com.jc.proto.conf;

import java.util.List;

public class ServerMod {
	
	String id;

	String name;
	
	String scheme;
	
	String host;
	
	Integer port;
	
	String proxy_entry;
	
	String proxy_path;
	
	String domain_visit;
	
	String logs;
	
	String libs;
	
	String master;
	
	List<AppMod> apps;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getProxy_entry() {
		return proxy_entry;
	}

	public void setProxy_entry(String proxy_entry) {
		this.proxy_entry = proxy_entry;
	}

	public String getProxy_path() {
		return proxy_path;
	}

	public void setProxy_path(String proxy_path) {
		this.proxy_path = proxy_path;
	}

	public String getLogs() {
		return logs;
	}

	public void setLogs(String logs) {
		this.logs = logs;
	}

	public List<AppMod> getApps() {
		return apps;
	}

	public void setApps(List<AppMod> apps) {
		this.apps = apps;
	}

	public String getLibs() {
		return libs;
	}

	public void setLibs(String libs) {
		this.libs = libs;
	}

	public String getMaster() {
		return master;
	}

	public void setMaster(String master) {
		this.master = master;
	}
	
	public String getDomain_visit() {
		return domain_visit;
	}

	public void setDomain_visit(String domain_visit) {
		this.domain_visit = domain_visit;
	}

	public boolean cocheck() {
		if(this.id==null) {
			return false;
		}
		return true;
	}
}
