package com.jeancoder.root.server.proto.msg;

@SuppressWarnings("serial")
public class ReplyServerBody extends ReplyBody {
	
	private String serverInfo;

	public ReplyServerBody(String serverInfo) {
		this.serverInfo = serverInfo;
	}

	public String getServerInfo() {
		return serverInfo;
	}

	public void setServerInfo(String serverInfo) {
		this.serverInfo = serverInfo;
	}
}
