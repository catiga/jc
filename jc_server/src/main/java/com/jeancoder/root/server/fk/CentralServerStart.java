package com.jeancoder.root.server.fk;

import java.io.BufferedReader;
import java.io.File;
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

		String masterDomain = null;
		for (ServerMod sm : fk_con.getServers()) {
			if(sm.getScheme().equalsIgnoreCase("http")) {
				masterDomain = sm.getMaster();
				// need modify base directory while initial starting, and if there are more than one hot loaded directories, means error and might need remove one of them manually
				for(AppMod server_app : sm.getApps()) {
					if(server_app.getApp_code().equals("server")) {
						String base_path = server_app.getApp_base();
						File f = new File(base_path);
						if(f.isDirectory() && f.listFiles() != null && f.listFiles().length == 1) {  //indicate that hot reloaded
							for(File file_item : f.listFiles()) {
								String real_path = file_item.getPath();
								logger.info("APP:::SERVER base path will be relocated to==={}", real_path);
								server_app.setApp_base(real_path);
							}
						}
					}
				}
			}
			JCServer server = ServerFactory.generate(sm);
			ServerHolder.getHolder().add(server);
			new Thread(server::start).start();
		}
		String finalMasterDomain = masterDomain;
		new Thread(new Runnable() {
			@Override
			public void run() {
				logger.info("$$$$$$ 中央服务线程启动中 $$$$$$");
				try {
					Thread.sleep(15000);
					String json = "";
					try {
						String rules = RemoteUtil.getConfigList(finalMasterDomain);
						ByteResults byteResults = JackSonBeanMapper.fromJson(rules, ByteResults.class);
						if (!"0000".equals(byteResults.getStatus())) {
                            logger.error("****** 配置文件解析错误:{} msg:{}", byteResults.getStatus(), byteResults.getMsg());
							return;
						}
						json = new String(byteResults.getResults());
					} catch (Exception e) {
						logger.error("****** 网络配置文件加载失败:", e);
					}
					try {
						if(!json.isEmpty()) {
							Gson gson = new Gson();
							FkConf fk_con = gson.fromJson(json, FkConf.class);
							JCVM jcvm = JCVMDelegatorGroup.instance().getDelegator().getVM();
							for (ServerMod sm : fk_con.getServers()) {
								if (!sm.getScheme().equalsIgnoreCase(ServerCode.HTTP.toString())) {
									continue;
								}
								for (AppMod mod : sm.getApps()) {
                                    logger.info("{}:::{}:::{}:::{}", mod.getApp_id(), mod.getApp_code(), mod.getApp_base(), mod.getFetch_address());
									JCAPP app = mod.to();
									try {
										InputStream ins = RemoteUtil.installation(mod.getFetch_address(), Long.valueOf(mod.getApp_id()));
										jcvm.uninstallApp(app);
										String new_path = ZipUtil.init_install(mod, new ZipInputStream(ins));
										app.setApp_base(new_path);
										jcvm.installApp(app);
									} catch (Exception e) {
                                        logger.error("install app error:{}, directory:{}", mod.getApp_code(), app.getApp_base(), e);
									}
								}
							}
						} else {
							logger.info("****** 仅加载本地管理应用框架.");
						}
					} catch (Exception e) {
						logger.error("****** 仅加载本地管理应用框架.", e);
					}
				} catch (InterruptedException e) {
					logger.error("Thread sleep interrupted.", e);
				}
			}
		}).start();
	}
	
}
