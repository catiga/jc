package com.jc.proto.msg.paramdebug;

import com.jc.proto.msg.MsgType;
import com.jc.proto.msg.SyncMsg;

// 接收参数方，非同步
@SuppressWarnings("serial")
public class ParamHandler extends SyncMsg {

	ParamMod params;
	
	String result;
	
	public ParamHandler() {
		super();
        setType(MsgType.INSPARAD);
	}

	public ParamMod getParams() {
		return params;
	}

	public void setParams(ParamMod params) {
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
