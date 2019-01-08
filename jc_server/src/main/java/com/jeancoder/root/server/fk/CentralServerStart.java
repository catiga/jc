package com.jeancoder.root.server.fk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.jc.proto.conf.FkConf;
import com.jc.proto.conf.ServerMod;
import com.jeancoder.root.server.inet.JCServer;
import com.jeancoder.root.server.inet.ServerFactory;
import com.jeancoder.root.server.state.ServerHolder;

public class CentralServerStart extends ExternalStarter {

	private static Logger logger = LoggerFactory.getLogger(CentralServerStart.class);

	public static void main(String[] argc) {
		centralServerStart();
	}
	/**
	 * 中央服务器启动测试环境å
	 */
	public static void centralServerStart() {
		String json = loadLocal();
		if (json == null) {
			logger.error("配置文件不存在");
			System.exit(-1);
			return;
		}
		FkConf fk_con = null;
		try {
			Gson gson = new Gson();
			fk_con = gson.fromJson(json, FkConf.class);
			if (fk_con == null || fk_con.getServers() == null || fk_con.getServers().isEmpty()) {
				throw new RuntimeException("EMPTY");
			}
		} catch (Exception e) {
			logger.error("start error:", e);
			System.exit(-1);
		}
		for (ServerMod sm : fk_con.getServers()) {
			JCServer server = ServerFactory.generate(sm);
			ServerHolder.getHolder().add(server);
			new Thread(new Runnable() {

				@Override
				public void run() {
					server.start();
				}
			}).start();
		}
	}
	
}
