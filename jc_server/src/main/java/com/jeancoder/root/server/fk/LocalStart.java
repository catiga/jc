package com.jeancoder.root.server.fk;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.jc.proto.conf.AppMod;
import com.jc.proto.conf.FkConf;
import com.jc.proto.conf.ServerMod;
import com.jeancoder.core.util.JackSonBeanMapper;
import com.jeancoder.root.server.inet.JCServer;
import com.jeancoder.root.server.inet.ServerCode;
import com.jeancoder.root.server.inet.ServerFactory;
import com.jeancoder.root.server.mixture.ByteResults;
import com.jeancoder.root.server.state.ServerHolder;
import com.jeancoder.root.server.util.RemoteUtil;
import com.jeancoder.root.server.util.ZipUtil;

public class LocalStart extends ExternalStarter {

	private static Logger logger = LoggerFactory.getLogger(LocalStart.class);

	/**
	 * -- argc commandline parameter
	 * 
	 * @param argc
	 */
	public static void main(String[] argc) {
		Scanner input = new Scanner(System.in);
		String val = null;
		do {
			val = input.next();
			if (val.equals("start")) {
				start();
			} else if (val.equals("list")) {
				Enumeration<JCServer> servers = ServerHolder.getHolder().servers();
				while(servers.hasMoreElements()) {
					logger.info("running server: " + servers.nextElement());
				}
			} else if (val.equals("stop")) {
				logger.info("preparing to shutdown server");
				Enumeration<JCServer> servers = ServerHolder.getHolder().servers();
				while(servers.hasMoreElements()) {
					JCServer handler = servers.nextElement();
					if (handler.defServerCode() == ServerCode.HTTP) {
						handler.shutdown();
					}
				}
			} else if (val.equals("master")) {
				logger.info("connect and register service to master");

			}
		} while (!val.equals("q")); // 如果输入的值不是#就继续输入
		input.close(); // 关闭资源
	}

	public static void start() {
		String json = null;
		try {
			// 本地读取配置文件
			InputStream ins = LocalStart.class.getClassLoader().getResourceAsStream(appConf);
			BufferedReader reader = new BufferedReader(new InputStreamReader(ins));

			String lineContent = null;
			StringBuffer buff = new StringBuffer();
			while ((lineContent = reader.readLine()) != null) {
				buff.append(lineContent);
			}
			json = buff.toString();
		} catch (Exception e) {
		}
		if (json == null) {
			try {
				String rules = RemoteUtil.getConfigList();
				ByteResults byteResults = JackSonBeanMapper.fromJson(rules, ByteResults.class);
				if (!"0000".equals(byteResults.getStatus())) {
					logger.info("获取配置失败 status:" + byteResults.getStatus() + " msg:" + byteResults.getMsg());
					return;
				}
				json = new String(byteResults.getResults());
			} catch (Exception e) {
				logger.error("网络加载配置文件错误",e);
			}
		}
		if (json == null) {
			logger.error("配置文件不存在");
			System.exit(-1);
			return;
		}
		try {
			Gson gson = new Gson();
			FkConf fk_con = gson.fromJson(json, FkConf.class);
			for (ServerMod sm : fk_con.getServers()) {
				JCServer server = ServerFactory.generate(sm);
				ServerHolder.getHolder().add(server);
				new Thread(new Runnable() {

					@Override
					public void run() {
						for (AppMod mod : sm.getApps()) {
							try {
								if (mod.getFetch_address() != null) {
									System.out.println("开始下载");
									InputStream ins = RemoteUtil.installation(mod.getFetch_address(),new Long(mod.getApp_id()));
									System.out.println("下载");
									ZipUtil.init_install(mod, new ZipInputStream(ins));
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						server.start();
					}
				}).start();
			}
		} catch (Exception e) {
			logger.error("start error:", e);
			System.exit(-1);
		}
	}
}
