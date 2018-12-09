package com.jc.proto.msg;

import java.util.UUID;

@SuppressWarnings("serial")
public class SyncMsg extends GeneralMsg {

	public SyncMsg() {
		super();
		this.unionid = UUID.randomUUID().toString().replace("-", "");
	}
}
