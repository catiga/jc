package com.jc.proto.msg.ct;

import com.jc.proto.msg.MsgType;
import com.jc.proto.msg.SyncMsg;

@SuppressWarnings("serial")
public class ViewContTblMsg extends SyncMsg {

	public ViewContTblMsg() {
		super();
		setType(MsgType.HANDLER_TABLES);
	}

	@Override
	public Object getResData() {
		return null;
	}
	
}
