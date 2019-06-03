package com.jeancoder.root.server.fk;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.jc.proto.conf.AppMod;
import com.jc.proto.conf.FkConf;
import com.jc.proto.conf.ServerMod;
import com.jeancoder.core.util.JackSonBeanMapper;
import com.jeancoder.root.env.JCAPP;
import com.jeancoder.root.server.inet.JCServer;
import com.jeancoder.root.server.inet.ServerCode;
import com.jeancoder.root.server.inet.ServerFactory;
import com.jeancoder.root.server.mixture.ByteResults;
import com.jeancoder.root.server.state.GlobalStateHolder;
import com.jeancoder.root.server.state.ServerHolder;
import com.jeancoder.root.server.util.RemoteUtil;
import com.jeancoder.root.server.util.ZipUtil;
import com.jeancoder.root.vm.JCVM;
import com.jeancoder.root.vm.JCVMDelegatorGroup;

public class CentralServerStart extends ExternalStarter {

	private static Logger logger = LoggerFactory.getLogger(CentralServerStart.class);

	public static void main(String[] argc) {
		centralServerStart();
	}
	/**
	 * 中央服务器启动
	 */

	public static void centralServerStart() {
		String json = null;
		try {
			// 本地读取配置文件
			InputStream ins = Starter.class.getClassLoader().getResourceAsStream(appConf);
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
			logger.error("本地初始化配置文件未找到.");
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
			if(fk_con.getIns_perf()!=null) {
				if(fk_con.getIns_perf().getVs_switch()!=null) {
					GlobalStateHolder.INSTANCE.setVsSwitch(fk_con.getIns_perf().getVs_switch());
				}
				if(fk_con.getIns_perf().getReq_handle_timeout()!=null) {
					GlobalStateHolder.INSTANCE.setInternalExecuteTimeout(fk_con.getIns_perf().getReq_handle_timeout());
				}
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
		new Thread(new Runnable() {
			@Override
			public void run() {
				logger.info("$$$$$$ 中央服务线程启动中 $$$$$$");
				try {
					Thread.sleep(15000);
					String json = "";
					try {
						String rules = RemoteUtil.getConfigList();
						ByteResults byteResults = JackSonBeanMapper.fromJson(rules, ByteResults.class);
						if (!"0000".equals(byteResults.getStatus())) {
							logger.error("****** 配置文件解析错误:" + byteResults.getStatus() + " msg:" + byteResults.getMsg());
							return;
						}
						json = new String(byteResults.getResults());
					} catch (Exception e) {
						logger.error("****** 网络配置文件加载失败:", e);
					}
					try {
						Gson gson = new Gson();
						FkConf fk_con = gson.fromJson(json, FkConf.class);
						JCVM jcvm = JCVMDelegatorGroup.instance().getDelegator().getVM();
						for (ServerMod sm : fk_con.getServers()) {
							if (!sm.getScheme().equalsIgnoreCase(ServerCode.HTTP.toString())) {
								continue;
							}
							for (AppMod mod : sm.getApps()) {
								logger.info(mod.getApp_id() + ":::" + mod.getApp_code() + ":::" + mod.getApp_base() + ":::" + mod.getFetch_address());
								JCAPP app = mod.to();
								try {
									InputStream ins = RemoteUtil.installation(mod.getFetch_address(), new Long(mod.getApp_id()));
									jcvm.uninstallApp(app);
									String new_path = ZipUtil.init_install(mod, new ZipInputStream(ins));
									app.setApp_base(new_path);
									jcvm.installApp(app);
								} catch (Exception e) {
									logger.error("install app error:" + mod.getApp_code() + ", directory:" + app.getApp_base(), e);
								}
							}
						}
					} catch (Exception e) {
						logger.error("start error:", e);
						System.exit(-1);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	
}
