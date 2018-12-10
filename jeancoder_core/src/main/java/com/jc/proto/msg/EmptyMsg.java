package com.jc.proto.msg;

@SuppressWarnings("serial")
public class EmptyMsg extends GeneralMsg {

	public EmptyMsg() {
		super();
		this.unionid = "EMPTY";
		setType(MsgType.EMPTY);
	}
}
