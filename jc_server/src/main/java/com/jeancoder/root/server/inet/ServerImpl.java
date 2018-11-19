package com.jeancoder.root.server.inet;

import java.util.ArrayList;
import java.util.List;

import com.jeancoder.root.container.JCVM;
import com.jeancoder.root.container.core.StandardVM;
import com.jeancoder.root.container.model.JCAPP;
import com.jeancoder.root.server.proto.conf.AppMod;
import com.jeancoder.root.server.proto.conf.ServerMod;

public abstract class ServerImpl implements JCServer {

	protected ServerMod modconf;
	
	public static JCVM jcvm = StandardVM.getVM();
	
	private volatile int countRef = 0;
	
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
					String ampk = am.getApp_id() + am.getApp_code();
					JCAPP jcapp = null;
					for(String k : jcvm.getContainers().keySet()) {
						if(!k.equals(ampk)) {
							jcapp = am.to();
						}
					}
					if(jcapp==null) {
						jcapp = am.to();
						jcapp.setLogbase(modconf.getLogs());
						countRef++;
						convert_proto.add(jcapp);
					}
				}
			}
			jcvm.setInitApps(convert_proto);
			jcvm.onStart();
		}
	}

	@Override
	public void shutdown() {
		synchronized (this) {
			jcvm.getContainers().forEach((k,v) -> {
				if(countRef>0) {
					countRef--;
				}
				v.onStop();
			});
		}
	}
}
