package com.jc.proto.msg;

import java.util.UUID;

@SuppressWarnings("serial")
public class SyncMsg extends GeneralMsg {

	public SyncMsg() {
		super();
		this.setType(MsgType.EXCHANGE);	//set common
		this.unionid = UUID.randomUUID().toString().replace("-", "");
	}
}
