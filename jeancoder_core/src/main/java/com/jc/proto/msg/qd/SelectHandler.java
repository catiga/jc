package com.jc.proto.msg.qd;

import com.jc.proto.msg.MsgType;

@SuppressWarnings("serial")
public class SelectHandler extends DataHandler {
	
	private String sql;

	public SelectHandler(String insid, String cont_id, String cont_code) {
		super(insid, cont_id, cont_code);
		setType(MsgType.HANDLER_SELECT);
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}
	
}
