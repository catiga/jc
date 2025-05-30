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

public class LocalStarter extends ExternalStarter {

	private static Logger logger = LoggerFactory.getLogger(LocalStarter.class);

	/**
	 * -- argc commandline parameter
	 * 
	 * @param argc
	 */
	public static void main(String[] argc) {
		start();
		// 中央服务打包启动方法
		//centralServerStart();
	}
	
	public static String loadLocal() {
		try {
			// 本地读取配置文件
			InputStream ins = Starter.class.getClassLoader().getResourceAsStream(appConfLocal);
			BufferedReader reader = new BufferedReader(new InputStreamReader(ins));

			String lineContent = null;
			StringBuffer buff = new StringBuffer();
			while ((lineContent = reader.readLine()) != null) {
				buff.append(lineContent);
			}
			return buff.toString();
		} catch (Exception e) {
			logger.error("unable to manage to load local configuration file {}. will terminate", appConfLocal, e);
			return null;
		}
	}

	public static void start() {
		String json = loadLocal();
		if (json == null) {
			try {
				String rules = RemoteUtil.getConfigList();
				ByteResults byteResults = JackSonBeanMapper.fromJson(rules, ByteResults.class);
				if (!"0000".equals(byteResults.getStatus())) {
					logger.info("load config error status:" + byteResults.getStatus() + " msg:" + byteResults.getMsg());
					return;
				}
				json = new String(byteResults.getResults());
			} catch (Exception e) {
				logger.error("checking and loading network error.", e);
			}
		}
		if (json == null) {
			logger.error("configs error and will exit.");
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
					for (AppMod mod : sm.getApps()) {
						try {
							if (mod.getFetch_address() != null) {
								logger.info("syncing apps config." + mod.getApp_code());
								InputStream ins = RemoteUtil.installation(mod.getFetch_address(), Long.valueOf(mod.getApp_id()));
								logger.info("synced apps config." + mod.getApp_code());
								ZipUtil.init_install(mod, new ZipInputStream(ins));
							}
						} catch (Exception e) {
							logger.error("can not fetch from remote platform, due to specify fetch address for app: {}", mod.getApp_name(), e);
						}
					}
					server.start();
				}
			}).start();
		}
	}

	/**
	 * 中央服务器启动测试环境
	 */
	public static void centralServerStart() {
		String json = null;
		try {
			// 本地读取配置文件
			InputStream ins = LocalStarter.class.getClassLoader().getResourceAsStream(appConf);
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
		new Thread(new Runnable() {
			@Override
			public void run() {
				logger.error("线程启动");
				try {
					Thread.sleep(15000);
					String json = "";
					try {
						String rules = RemoteUtil.getConfigList();
						ByteResults byteResults = JackSonBeanMapper.fromJson(rules, ByteResults.class);
						if (!"0000".equals(byteResults.getStatus())) {
							logger.info("获取配置失败 status:" + byteResults.getStatus() + " msg:" + byteResults.getMsg());
							return;
						}
						json = new String(byteResults.getResults());
					} catch (Exception e) {
						logger.error("网络加载配置文件错误", e);
					}
					try {
						Gson gson = new Gson();
						FkConf fk_con = gson.fromJson(json, FkConf.class);
						for (ServerMod sm : fk_con.getServers()) {
							if (!sm.getScheme().equalsIgnoreCase(ServerCode.HTTP.toString())) {
								continue;
							}
							for (AppMod mod : sm.getApps()) {
								try {
									InputStream ins = RemoteUtil.installation(mod.getFetch_address(), Long.valueOf(mod.getApp_id()));
									ZipUtil.init_install(mod, new ZipInputStream(ins));
									JCVM jcvm = JCVMDelegatorGroup.instance().getDelegator().getVM();
									jcvm.installApp(mod.to());
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
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
