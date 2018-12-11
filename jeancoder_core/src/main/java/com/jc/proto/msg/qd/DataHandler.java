package com.jc.proto.msg.qd;

import com.jc.proto.msg.SyncMsg;

@SuppressWarnings("serial")
public abstract class DataHandler extends SyncMsg {

	private String insid;
	
	private String contid;
	
	private String contcode;
	
	private Object data;
	
	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public DataHandler(String insid, String cont_id, String cont_code) {
		this.insid = insid;
		this.contid = cont_id;
		this.contcode = cont_code;
	}

	public String getInsid() {
		return insid;
	}

	public void setInsid(String insid) {
		this.insid = insid;
	}

	public String getContid() {
		return contid;
	}

	public void setContid(String contid) {
		this.contid = contid;
	}

	public String getContcode() {
		return contcode;
	}

	public void setContcode(String contcode) {
		this.contcode = contcode;
	}
	
	@Override
	public Object getResData() {
		return getData();
	}
}
