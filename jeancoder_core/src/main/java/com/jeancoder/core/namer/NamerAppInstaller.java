package com.jeancoder.core.namer;


import com.jeancoder.core.log.JCLogger;
import com.jeancoder.core.log.JCLoggerFactory;
import com.jeancoder.core.resource.proc.Application;
import com.jeancoder.core.resource.runtime.ApplicationHolder;

public class NamerAppInstaller implements IAppInstaller{
	
	private static final JCLogger LOGGER = JCLoggerFactory.getLogger(NamerAppInstaller.class);
	
	private NamerFetch fetch;
	private NamerParse parse;
	private NamerLoad load;
	private NamerApplication applicationConfigure;
	    
	
	public NamerFetch getFetch() {
		return fetch;
	}
	public void setFetch(NamerFetch fetch) {
		this.fetch = fetch;
	}
	public NamerParse getParse() {
		return parse;
	}
	public void setParse(NamerParse parse) {
		this.parse = parse;
	}
	public NamerLoad getLoad() {
		return load;
	}
	public void setLoad(NamerLoad load) {
		this.load = load;
	}
	public NamerApplication getApplication() {
		return applicationConfigure;
	}
	public void setApplication(NamerApplication application) {
		this.applicationConfigure = application;
	}
	
	public Application install() {
		
		Application application = null;
		IFetchResult  fetchResult = fetch.fetch(applicationConfigure);
		LOGGER.debug("Application " + applicationConfigure.getAppCode() + " NamerFetch fetch success");
		
		String loadPath = load.loadContext(applicationConfigure, fetchResult);
		LOGGER.debug("Application " + applicationConfigure.getAppCode() + " NamerLoad loadContext success");
		
		application = parse.parse(applicationConfigure, loadPath);
		LOGGER.debug("Application " + applicationConfigure.getAppCode() + " NamerParse parse success");
		
		application.setApp(applicationConfigure);
		ApplicationHolder.getInstance().addApp(application);
		LOGGER.debug("Application " + applicationConfigure.getAppCode() + " ApplicationHolder addApp success");
			
		return application;
	}
}
