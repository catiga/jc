package com.jeancoder.root.server.proto.msg.ct;

import com.jeancoder.root.server.proto.conf.AppMod;
import com.jeancoder.root.server.proto.msg.GeneralMsg;
import com.jeancoder.root.server.proto.msg.MsgType;

@SuppressWarnings("serial")
public class UninstallMsg extends GeneralMsg {

	AppMod appins;

	public UninstallMsg(AppMod mod) {
        super();
        this.appins = mod;
        setType(MsgType.APPUNINSTALL);
    }
	
	public AppMod getAppins() {
		return appins;
	}

	public void setAppins(AppMod appins) {
		this.appins = appins;
	}
	
}
