package com.jc.proto.msg;

public enum MsgType {
	EMPTY, 
	
	PING, ASK, REPLY, LOGIN,
	
	APPUNINSTALL, APPINSTALL, APPUPGRADE, APPCONTAINERS,
	
	HANDLER_SELECT, HANDLER_TABLES,
	
	EXCHANGE,
	
	MONIT_REQ,
	
	//instance param debug
	INSPARAD
}
