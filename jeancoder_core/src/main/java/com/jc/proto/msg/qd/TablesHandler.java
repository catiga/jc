package com.jc.proto.msg.qd;

import com.jc.proto.msg.MsgType;

@SuppressWarnings("serial")
public class TablesHandler extends DataHandler {

	public TablesHandler(String insid, String cont_id, String cont_code) {
		super(insid, cont_id, cont_code);
		setType(MsgType.HANDLER_TABLES);
	}
	
}
