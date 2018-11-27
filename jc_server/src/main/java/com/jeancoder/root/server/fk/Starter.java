package com.jeancoder.root.server.fk;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.jeancoder.root.server.inet.JCServer;
import com.jeancoder.root.server.inet.ServerCode;
import com.jeancoder.root.server.inet.ServerFactory;
import com.jeancoder.root.server.proto.conf.FkConf;
import com.jeancoder.root.server.proto.conf.ServerMod;

public class Starter {

	private static Logger logger = LoggerFactory.getLogger(Starter.class);

	final static String appConf = "ins.server.json";

	final static List<JCServer> iservers = new ArrayList<JCServer>();

	/**
	 * -- argc commandline parameter
	 * 
	 * @param argc
	 */
	public static void main(String[] argc) {
		Scanner input = new Scanner(System.in);
		String val = null;
		do {
			val = input.next(); // 等待输入值
			if(val.equals("start")) {
//				Thread daemon = new Thread(new Runnable() {
//
//					@Override
//					public void run() {
//						// 用来检查服务，以及接受用户输入
//						while (true) {
//							try {
//								Thread.sleep(1000L);
//							} catch (InterruptedException e) {
//								e.printStackTrace();
//							}
//						}
//					}
//				});
//				daemon.setDaemon(true);
//				daemon.start();
				start();
			} else if(val.equals("list")) {
				for (JCServer handler : iservers) {
					System.out.println("running server: " + handler);
				}
			} else if(val.equals("stop")) {
				logger.info("preparing to shutdown server");
				for (JCServer handler : iservers) {
					if(handler.defServerCode()==ServerCode.HTTP) {
						handler.shutdown();
					}
				}
			}
		} while (!val.equals("q")); // 如果输入的值不是#就继续输入
		input.close(); // 关闭资源
		
		// Thread daemon = new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// //用来检查服务，以及接受用户输入
		// while(true) {
		// try {
		// Thread.sleep(1000L);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// for(JCServer handler : iservers) {
		// System.out.println("测试服务器句柄: " + handler);
		// }
		// }
		// }
		// });
		// daemon.setDaemon(true);
		// daemon.start();
		// start();
	}

	public static void start() {
		try {
			InputStream ins = Starter.class.getClassLoader().getResourceAsStream(appConf);
			BufferedReader reader = new BufferedReader(new InputStreamReader(ins));

			String lineContent = null;
			StringBuffer buff = new StringBuffer();
			while ((lineContent = reader.readLine()) != null) {
				buff.append(lineContent);
			}
			Gson gson = new Gson();
			FkConf fk_con = gson.fromJson(buff.toString(), FkConf.class);

			for (ServerMod sm : fk_con.getServers()) {
				JCServer server = ServerFactory.generate(sm);
				iservers.add(server);
				new Thread(new Runnable() {

					@Override
					public void run() {
						server.start();
					}
				}).start();
			}

		} catch (Exception e) {
			logger.error("start error:", e);
		}
	}
}
