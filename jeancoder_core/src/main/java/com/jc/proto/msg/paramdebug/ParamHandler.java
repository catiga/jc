package com.jc.proto.msg.paramdebug;

import com.jc.proto.msg.GeneralMsg;
import com.jc.proto.msg.MsgType;

// 接收参数方，非同步
@SuppressWarnings("serial")
public class ParamHandler extends GeneralMsg {

	ParamMod params;
	
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
	
}
