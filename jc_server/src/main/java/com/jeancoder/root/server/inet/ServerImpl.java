package com.jeancoder.root.server.inet;

import com.jeancoder.root.server.proto.conf.ServerMod;

public abstract class ServerImpl implements JCServer {

	protected ServerMod modconf;
	
	public ServerImpl() {
		modconf = new ServerMod();
		modconf.setProxy_entry("entry");
		modconf.setProxy_path("/");
		modconf.setServer_name("default server");
		modconf.setServer_port(12345);
		modconf.setServer_scheme(ServerCode.HTTP.toString());
	}
	
	public ServerImpl(ServerMod modconf) {
		super();
		this.modconf = modconf;
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

}
