package com.jeancoder.root.server.fk;

import java.io.InputStream;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.jc.proto.conf.AppMod;
import com.jc.proto.conf.FkConf;
import com.jc.proto.conf.ServerMod;
import com.jeancoder.core.util.JackSonBeanMapper;
import com.jeancoder.root.server.inet.JCServer;
import com.jeancoder.root.server.inet.ServerFactory;
import com.jeancoder.root.server.mixture.ByteResults;
import com.jeancoder.root.server.state.GlobalStateHolder;
import com.jeancoder.root.server.state.ServerHolder;
import com.jeancoder.root.server.util.RemoteUtil;
import com.jeancoder.root.server.util.ZipUtil;

public class Starter extends ExternalStarter {

	private static Logger logger = LoggerFactory.getLogger(Starter.class);

	/**
	 * -- argc commandline parameter
	 * 
	 * @param argc
	 */
	public static void main(String[] argc) {
		start();
	}

	public static void start() {
		String json = null;
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
								InputStream ins = RemoteUtil.installation(mod.getFetch_address(), new Long(mod.getApp_id()));
								logger.info("synced apps config." + mod.getApp_code());
								ZipUtil.unzip(mod.getApp_base(), new ZipInputStream(ins));
							}
						} catch (Exception e) {
							logger.error(mod.getApp_code() + " config error, will be continued.", e);
						}
					}
					server.start();
				}
			}).start();
		}
	}

}
