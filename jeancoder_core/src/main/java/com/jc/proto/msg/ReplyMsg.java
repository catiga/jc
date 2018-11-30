package com.jc.proto.msg;

@SuppressWarnings("serial")
public class ReplyMsg extends GeneralMsg {
	
	public ReplyMsg() {
		super();
		setType(MsgType.REPLY);
	}

	private ReplyBody body;

	public ReplyBody getBody() {
		return body;
	}

	public void setBody(ReplyBody body) {
		this.body = body;
	}
}
