package com.jeancoder.root.server.fk;

import java.net.URL;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.util.StatusPrinter;

public abstract class ExternalStarter {
	
	final static String appConf = "ins.server.json";
	
	final static String logConf = "logback.xml";

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
			e.printStackTrace();
		}
	}
	
}
