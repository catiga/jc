package com.jc.proto.msg.ct;

import com.jc.proto.msg.MsgType;
import com.jc.proto.msg.SyncMsg;

@SuppressWarnings("serial")
public class ViewVmContsMsg extends SyncMsg {

	public ViewVmContsMsg() {
		super();
		setType(MsgType.APPCONTAINERS);
	}
}
