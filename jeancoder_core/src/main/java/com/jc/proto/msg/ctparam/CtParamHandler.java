package com.jc.proto.msg.ctparam;

import com.jc.proto.msg.MsgType;
import com.jc.proto.msg.SyncMsg;

@SuppressWarnings("serial")
public class CtParamHandler extends SyncMsg {

	CtParamMod params;

	String result;

	public CtParamHandler() {
		super();
        setType(MsgType.CTINSPARAM);
	}
	
	public CtParamMod getParams() {
		return params;
	}

	public void setParams(CtParamMod params) {
		this.params = params;
	}

	public void success() {
		result = "0";
	}
	
	public void fail() {
		result = "1";
	}
	
	@Override
	public Object getResData() {
		return result;
	}
}
