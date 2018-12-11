package com.jc.proto.msg.qd;

import com.jc.proto.msg.MsgType;

@SuppressWarnings("serial")
public class SelectHandler extends DataHandler {

	public SelectHandler(String insid, String cont_id, String cont_code) {
		super(insid, cont_id, cont_code);
		setType(MsgType.HANDLER_SELECT);
	}
	
	@Override
	public Object getResData() {
		return null;
	}
	
}
