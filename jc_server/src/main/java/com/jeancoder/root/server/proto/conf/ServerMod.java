package com.jeancoder.root.server.proto.conf;

public class ServerMod {

	String server_name;
	
	String server_scheme;
	
	Integer server_port;
	
	String proxy_entry;
	
	String proxy_path;
	
	String logs;

	public String getServer_name() {
		return server_name;
	}

	public void setServer_name(String server_name) {
		this.server_name = server_name;
	}

	public String getServer_scheme() {
		return server_scheme;
	}

	public void setServer_scheme(String server_scheme) {
		this.server_scheme = server_scheme;
	}

	public Integer getServer_port() {
		return server_port;
	}

	public void setServer_port(Integer server_port) {
		this.server_port = server_port;
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
	
}
