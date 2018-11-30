package com.jc.proto.msg.ct;

import com.jc.proto.conf.AppMod;
import com.jc.proto.msg.GeneralMsg;
import com.jc.proto.msg.MsgType;

@SuppressWarnings("serial")
public class InstallMsg extends GeneralMsg {

	AppMod appins;

	public InstallMsg(AppMod mod) {
        super();
        this.appins = mod;
        setType(MsgType.APPINSTALL);
    }
	
	public AppMod getAppins() {
		return appins;
	}

	public void setAppins(AppMod appins) {
		this.appins = appins;
	}
	
}
