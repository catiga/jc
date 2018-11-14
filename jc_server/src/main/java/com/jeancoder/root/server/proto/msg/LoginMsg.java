package com.jeancoder.root.server.proto.msg;

@SuppressWarnings("serial")
public class LoginMsg extends GeneralMsg {
	
	private String userName;
	
	private String password;

	public LoginMsg() {
		super();
		setType(MsgType.LOGIN);
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
