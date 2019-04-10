package com.jc.proto.msg.monit;

import com.jc.proto.msg.MsgType;
import com.jc.proto.msg.SyncMsg;

@SuppressWarnings("serial")
public class ReqHandler extends SyncMsg {
	
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

	@Override
	public Object getResData() {
		return data;
	}
	
}
