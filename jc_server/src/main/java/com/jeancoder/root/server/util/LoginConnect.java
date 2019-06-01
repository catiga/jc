package com.jeancoder.root.server.util;

import com.jc.proto.msg.LoginMsg;
import com.jeancoder.root.server.mixture.JCLHealper;

public class LoginConnect {

	public static LoginMsg buildLoginMsg() {
		String userName = JCLHealper.INSTENSE.getMerchantsCode();
		String password = JCLHealper.INSTENSE.getInstanceNum();
		LoginMsg msg = new LoginMsg();
		msg.setUserName(userName);
		msg.setPassword(password);
		return msg;
	}
}
