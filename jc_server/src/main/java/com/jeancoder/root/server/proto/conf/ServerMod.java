package com.jeancoder.root.server.proto.conf;

import java.util.List;

public class ServerMod {

	String name;
	
	String scheme;
	
	String host;
	
	Integer port;
	
	String proxy_entry;
	
	String proxy_path;
	
	String logs;
	
	private String libs;
	
	List<AppMod> apps;

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
	
}
