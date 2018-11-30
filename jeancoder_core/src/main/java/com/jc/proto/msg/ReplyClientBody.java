package com.jc.proto.msg;

@SuppressWarnings("serial")
public class ReplyClientBody extends ReplyBody {
	
	private String clientInfo;

	public ReplyClientBody(String clientInfo) {
		this.clientInfo = clientInfo;
	}

	public String getClientInfo() {
		return clientInfo;
	}

	public void setClientInfo(String clientInfo) {
		this.clientInfo = clientInfo;
	}
}
