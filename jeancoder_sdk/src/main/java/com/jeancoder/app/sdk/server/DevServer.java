package com.jeancoder.app.sdk.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.jeancoder.app.sdk.Interceptor.JCInterceptorStack;
import com.jeancoder.app.sdk.configure.DevConfigureReader;
import com.jeancoder.app.sdk.configure.DevSysConfigProp;
import com.jeancoder.app.sdk.configure.DevSystemProp;
import com.jeancoder.app.sdk.context.ApplicationContextPool;
import com.jeancoder.app.sdk.context.JCApplicationContext;
import com.jeancoder.app.sdk.http.DevGatewayServlet;
import com.jeancoder.app.sdk.power.DevPowerLoader;
import com.jeancoder.core.common.Common;
import com.jeancoder.core.configure.JeancoderConfigurer;
import com.jeancoder.core.configure.PropType;
import com.jeancoder.core.exception.SdkRuntimeException;
import com.jeancoder.core.http.JCThreadLocal;
import com.jeancoder.core.namer.DevLang;
import com.jeancoder.core.namer.FetchWay;
import com.jeancoder.core.namer.InstallWay;
import com.jeancoder.core.namer.InstallerFactory;
import com.jeancoder.core.namer.NamerApplication;
import com.jeancoder.core.power.DatabasePower;
import com.jeancoder.core.power.DatabasePowerHandler;
import com.jeancoder.core.power.MemPower;
import com.jeancoder.core.power.MemPowerHandler;
import com.jeancoder.core.power.PowerHandlerFactory;
import com.jeancoder.core.power.PowerName;
import com.jeancoder.core.power.QiniuPower;
import com.jeancoder.core.power.QiniuPowerHandler;
import com.jeancoder.core.resource.proc.Application;
import com.jeancoder.core.resource.proc.Resource;
import com.jeancoder.core.resource.runtime.ApplicationHolder;

public class DevServer {
//	private static String MAVEN_STANDARD_SOURCE_CODE_PATH = "src"+File.separator+"main"+File.separator+"java";
	
	/**
	 * @param port 本地端口
	 * @param appPath 标准maven工程的工程路径
	 */
	public static void start(int port,String appPath) {
		try {
			DevConfigureReader.init(appPath, "appcode");
//			//初始化系统能力
			DevPowerLoader.init("appcode");
			DevSystemProp appcnf = (DevSystemProp)JeancoderConfigurer.fetchDefault(PropType.APPLICATION, "appcode");
			DevSysConfigProp sysConfig = (DevSysConfigProp)JeancoderConfigurer.fetchDefault(PropType.SYS, "appcode");
			
			NamerApplication app1 = new NamerApplication();
			app1.setAppCode(appcnf.getCode());
			app1.setDeveloperCode(appcnf.getDevelopercode());
			app1.setDevLang(DevLang.SDK_GROOVY);
			app1.setFetchWay(FetchWay.SDK);
			app1.setFetchAddress(appPath);
			app1.setInstallAddress(appPath);
			app1.setInstallWay(InstallWay.DISK);
			app1.setDescribe(appcnf.getDescribe());
			app1.setIndex(appcnf.getIndex());
			app1.setAppName(appcnf.getName());
			InstallerFactory.generateInstaller(app1).install();
			if (sysConfig != null) {
				app1.setInstallAddress(sysConfig.getLoadUri());
			}
			Application application = ApplicationHolder.getInstance().getAppByCode(app1.getAppCode());
		
//			//打印资源树
//			ApplicationHolder.getInstance().prinAll();
//			//初始应用相关的上下文
			JCApplicationContext jcApplicationContext = new JCApplicationContext();
			jcApplicationContext.setApplication(application);
			// db
			DatabasePowerHandler jcDatabasePower = (DatabasePowerHandler)PowerHandlerFactory.getPowerHandler(PowerName.DATABASE,"appcode");
			jcApplicationContext.add(Common.DATABASE,(DatabasePower)jcDatabasePower);
			// men
			MemPowerHandler memPowerHandler = (MemPowerHandler)PowerHandlerFactory.getPowerHandler(PowerName.MEM,"appcode");
			jcApplicationContext.add(Common.MEM_POWER,(MemPower)memPowerHandler);
			
			// 青牛
			QiniuPowerHandler  qiniuPowerHandler = (QiniuPowerHandler)PowerHandlerFactory.getPowerHandler(PowerName.QINIU,"appcode");
			jcApplicationContext.add(Common.QINIU_POWER,(QiniuPower)qiniuPowerHandler);
			ApplicationContextPool.addApplicationContext(app1.getAppCode(), jcApplicationContext);
			
			//注册拦截器
			//执行初始化文件
			JCInterceptorStack.setNamerApplication(app1);
			InstallerFactory.generateInstaller(app1).install();
			JCThreadLocal.setCode(app1.getAppCode());
			Resource resource  = application.getResource(Common.INITIAL, Common.INITIAL);
			if (resource != null) {
				resource.getResult();
			}
			
			Server server = new Server(port);
			ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
			context.setContextPath("/");
			context.setResourceBase(appPath);
	        server.setHandler(context);
	        context.addServlet(new ServletHolder(new DevGatewayServlet()),"/*");
			server.start();
		} catch (Exception e) { 
			throw new SdkRuntimeException("DevServer start failure", e);
		}
	}
	
	public static void main(String[] args) {
		String appPath = "file://///Users/huangjie/workspace/mixture-system";
		//读取本地配置
		DevServer.start(8080,appPath);
	}
}
