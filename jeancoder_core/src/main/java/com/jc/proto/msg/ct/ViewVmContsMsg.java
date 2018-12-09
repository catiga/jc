package com.jc.proto.msg.ct;

import com.jc.proto.msg.GeneralMsg;
import com.jc.proto.msg.MsgType;

@SuppressWarnings("serial")
public class ViewVmContsMsg extends GeneralMsg {

	public ViewVmContsMsg() {
		super();
		setType(MsgType.APPCONTAINERS);
	}
}
