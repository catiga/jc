package com.jeancoder.root.server.proto.msg;

@SuppressWarnings("serial")
public class PingMsg extends GeneralMsg {
	public PingMsg() {
		super();
		setType(MsgType.PING);
	}
}
