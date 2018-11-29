package com.jeancoder.root.server.inet;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeancoder.root.container.core.BCID;
import com.jeancoder.root.env.JCAPP;
import com.jeancoder.root.server.proto.conf.AppMod;
import com.jeancoder.root.server.proto.conf.ServerMod;
import com.jeancoder.root.server.ugr.MasterLiveBuilder;

public abstract class ServerImpl extends AbstractServer implements JCServer {
	
	private static Logger logger = LoggerFactory.getLogger(ServerImpl.class);

	protected ServerMod modconf;
	
	protected MasterLiveBuilder masterHandler;
	
	public ServerImpl() {
		modconf = new ServerMod();
		modconf.setProxy_entry("entry");
		modconf.setProxy_path("/");
		modconf.setName("default server");
		modconf.setPort(12345);
		modconf.setScheme(ServerCode.HTTP.toString());
		modconf.setLibs("/Users/jackielee/Desktop/logs");
	}
	
	public ServerImpl(ServerMod modconf) {
		super();
		this.modconf = modconf;
	}

	@Override
	public ServerMod info() {
		return this.modconf;
	}
	
	@Override
	public String serverId() {
		return this.modconf.getId();
	}
	
	@Override
	public void start() {
		synchronized (this) {
			// start delegate service, and just man HTTP
			if(this.defServerCode().equals(ServerCode.HTTP)&&this.modconf.getMaster()!=null&&!this.modconf.getId().startsWith("master")) {
				try {
					masterHandler = new MasterLiveBuilder(this.modconf);
					masterHandler.connect();
				} catch (Exception e) {
					logger.error("", e);
					throw new RuntimeException(e);
				}
			}
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
			getVM().bindLibrary(modconf.getLibs());
			getVM().setInitApps(convert_proto);
			getVM().onStart();
		}
	}

	@Override
	public void shutdown() {
		synchronized (this) {
			getVM().onStop();
			getVM().onDestroy();
		}
	}
}
