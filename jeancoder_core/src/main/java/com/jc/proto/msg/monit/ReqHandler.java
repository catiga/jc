package com.jc.proto.msg.monit;

import com.jc.proto.msg.GeneralMsg;
import com.jc.proto.msg.MsgType;

@SuppressWarnings("serial")
public class ReqHandler extends GeneralMsg {
	
	Object data;

	public ReqHandler() {
		super();
        setType(MsgType.MONIT_REQ);
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
}
