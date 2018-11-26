package com.jeancoder.root.server.inet;

import java.util.ArrayList;
import java.util.List;

import com.jeancoder.root.container.core.BCID;
import com.jeancoder.root.env.JCAPP;
import com.jeancoder.root.server.proto.conf.AppMod;
import com.jeancoder.root.server.proto.conf.ServerMod;

public abstract class ServerImpl extends AbstractServer implements JCServer {

	protected ServerMod modconf;
	
	public ServerImpl() {
		modconf = new ServerMod();
		modconf.setProxy_entry("entry");
		modconf.setProxy_path("/");
		modconf.setName("default server");
		modconf.setPort(12345);
		modconf.setScheme(ServerCode.HTTP.toString());
	}
	
	public ServerImpl(ServerMod modconf) {
		super();
		this.modconf = modconf;
	}

	@Override
	public void start() {
		synchronized (this) {
			List<AppMod> apps = this.modconf.getApps();
			List<JCAPP> convert_proto = new ArrayList<>();
			if(apps!=null) {
				for(AppMod am : apps) {
					//String amid = am.getApp_id();
					String amcode = am.getApp_code();
					JCAPP jcapp = null;
					for(BCID k : getVM().getContainers().keySet()) {
						if(!k.code().equals(amcode)) {
							jcapp = am.to();
						}
					}
					if(jcapp==null) {
						jcapp = am.to();
						jcapp.setLogbase(modconf.getLogs());
						convert_proto.add(jcapp);
					}
				}
			}
			getVM().setInitApps(convert_proto);
			getVM().onStart();
		}
	}

	@Override
	public void shutdown() {
		synchronized (this) {
			getVM().getContainers().shutdown();
		}
	}
}
