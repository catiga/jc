package com.jeancoder.root.server.inet;

import com.jc.proto.conf.ServerMod;
import com.jeancoder.root.server.comm.http.JCHttpServer;
import com.jeancoder.root.server.comm.socket.JCSocketServer;
import com.jeancoder.root.server.comm.ws.JCWebSocketServer;

public class ServerFactory {

	public static JCServer generate(ServerMod mod) {
		if(mod.getScheme().equalsIgnoreCase(ServerCode.SOCKET.toString())) {
			return new JCSocketServer(mod);
		} else if(mod.getScheme().equalsIgnoreCase(ServerCode.HTTP.toString())) {
			return new JCHttpServer(mod);
		} else if(mod.getScheme().equalsIgnoreCase(ServerCode.WS.toString())) {
			return new JCWebSocketServer(mod);
		}
		throw new RuntimeException("unsupport server code.");
	}
}
