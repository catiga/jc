package com.jeancoder.root.server.fk;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import com.jc.proto.conf.AppMod;
import com.jc.proto.conf.FkConf;
import com.jc.proto.conf.ServerMod;
import com.jeancoder.root.server.config.CustomCmdConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.util.StatusPrinter;

public abstract class ExternalStarter {

	private static Logger logger = LoggerFactory.getLogger(ExternalStarter.class);
	
	final static String appConf = "ins.server.json";
	
	final static String appConfLocal = "ins.server.localtest.json";

	final static String appConfLocalCentral = "ins.server.localcentral.json";
	
	final static String logConf = "logback.xml";

	private static String customConfigFile;

	public static final String CUSTOM_MANAGE_DOMAIN = "custom_manage_domain";
	public static final String CUSTOM_FETCH_DOMAIN = "custom_fetch_domain";

	protected static void initCustom(FkConf fk_con) {
		String configPath = System.getProperty("custom.config");
		logger.info("load custom config path from cmd: {}", configPath);
		if (configPath != null) {
			Properties props = new Properties();
			try (FileInputStream fis = new FileInputStream(configPath)) {
				props.load(fis);
			} catch (IOException e) {
				logger.error("init custom error, ignore it.", e);
			}

			String custom_manage_domain = props.getProperty(CUSTOM_MANAGE_DOMAIN);
			String custom_fetch_domain = props.getProperty(CUSTOM_FETCH_DOMAIN);

			CustomCmdConf config = CustomCmdConf.getInstance();
			config.setFetchDomain(custom_fetch_domain);
			config.setManageDomain(custom_manage_domain);
			logger.info("custom_domains. manage:{}, fetch:{}", custom_manage_domain, custom_fetch_domain);

			if (fk_con != null && fk_con.getServers() != null && !fk_con.getServers().isEmpty()) {
				for (ServerMod sm :  fk_con.getServers()) {
					if ("http".equals(sm.getScheme())) {
						String master = sm.getMaster();	// jcs://domain:port/
						if (config.getManageDomain() != null) {
							sm.setMaster(config.getManageDomain());
						}
						if (sm.getApps() != null && !sm.getApps().isEmpty() && config.getFetchDomain() != null) {
							for (AppMod am : sm.getApps()) {
								try {
									String newOne = rewriteUrl(am.getFetch_address(), config.getFetchDomain());
									am.setFetch_address(newOne);
								} catch (Exception e) {
									logger.error("custom_domains. fetch address config error {}", config.getFetchDomain(), e);
								}
							}
						}
					}
				}
			}
		}
	}

	static {
		try {
			LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
			URL externalConfigFileLocation = Thread.currentThread().getContextClassLoader().getResource(logConf);
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(lc);
			lc.reset();
			configurator.doConfigure(externalConfigFileLocation);
			StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
		}catch(Exception e) {
			logger.error("log init error.", e);
		}
	}
	
	public static String loadLocal() {
		try {
			// 本地读取配置文件
			InputStream ins = Starter.class.getClassLoader().getResourceAsStream(appConf);
			BufferedReader reader = new BufferedReader(new InputStreamReader(ins));

			String lineContent = null;
			StringBuffer buff = new StringBuffer();
			while ((lineContent = reader.readLine()) != null) {
				buff.append(lineContent);
			}
			return buff.toString();
		} catch (Exception e) {
			logger.error("init local file error.", e);
			return null;
		}
	}

	public static String rewriteUrl(String originalUrl, String newBaseUrl) throws URISyntaxException {
		if (originalUrl == null) {
			return null;
		}
		URI original = new URI(originalUrl);
		URI base = new URI(newBaseUrl);

		URI rewritten = new URI(
				base.getScheme(),    // new scheme (e.g., https)
				base.getUserInfo(),
				base.getHost(),      // new domain
				base.getPort(),
				original.getPath(),  // original path
				original.getQuery(), // original query
				original.getFragment()
		);

		return rewritten.toString();
	}

	public static void main(String[] argc) throws Exception {
		String newOne = rewriteUrl("http://jcloudapp.chinaren.xyz/server/api/personal/common/installation?foo=bar", "https://jc.jean.io");
		System.out.println(newOne);
	}
	
}
