package com.jc.proto.msg;

import java.util.UUID;

@SuppressWarnings("serial")
public abstract class SyncMsg extends GeneralMsg {

	public SyncMsg() {
		super();
		this.setType(MsgType.EXCHANGE);	//set common
		this.unionid = UUID.randomUUID().toString().replace("-", "");
	}
	
	public abstract Object getResData();
}
