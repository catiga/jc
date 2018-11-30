package com.jeancoder.app.sdk.source.jeancoder;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.jc.channel.SlaveCli;
import com.jc.shell.ShellServer;
import com.jeancoder.app.sdk.Interceptor.JCInterceptorStack;
import com.jeancoder.app.sdk.configure.DevSysConfigProp;
import com.jeancoder.app.sdk.context.ApplicationContextPool;
import com.jeancoder.app.sdk.context.JCApplicationContext;
import com.jeancoder.app.sdk.source.dto.NamerApplicationDto;
import com.jeancoder.core.common.Common;
import com.jeancoder.core.configure.JeancoderConfigurer;
import com.jeancoder.core.configure.PropType;
import com.jeancoder.core.log.JCLogger;
import com.jeancoder.core.log.JCLoggerFactory;
import com.jeancoder.core.namer.DevLang;
import com.jeancoder.core.namer.FetchWay;
import com.jeancoder.core.namer.InstallWay;
import com.jeancoder.core.namer.InstallerFactory;
import com.jeancoder.core.namer.NamerApplication;
import com.jeancoder.core.power.DatabasePower;
import com.jeancoder.core.power.DatabasePowerHandler;
import com.jeancoder.core.power.PowerHandlerFactory;
import com.jeancoder.core.power.PowerName;
import com.jeancoder.core.resource.proc.Application;
import com.jeancoder.core.resource.proc.Resource;
import com.jeancoder.core.resource.runtime.ApplicationHolder;
import com.jeancoder.core.util.InputStreamUtil;
import com.jeancoder.root.container.ContainerMaps;
import com.jeancoder.root.container.JCAppContainer;
import com.jeancoder.root.container.core.BCID;
import com.jeancoder.root.env.JCAPP;
import com.jeancoder.root.vm.JCVMDelegatorGroup;
import com.jeancoder.root.vm.VMDelegate;

/**
 * ApplicationSource 只有指定的jeancoder 开发应用才能使用
 * @author huangjie
 *
 */
public class ApplicationSource extends SysSource {
	
	private static final JCLogger LOGGER = JCLoggerFactory.getLogger(ApplicationSource.class);

	/**
	 * 返回安装路径
	 * @return
	 */
	public static void setInputStream(String url, InputStream inputStream) {
		InputStreamUtil.close();
		InputStreamUtil.put(url, inputStream);
	}
	
	public static void close() {
		InputStreamUtil.close();
	}
	
	/**
	 * 安装一个应用
	 * 生成 一个NamerApplication
	 * 然后 获取 NamerAppInstaller 安装器
	 * 最后进行安装
	 * 
	 * 获取对数据库操作的句柄
	 *  加载拦截器
	 * 初始应用相关的上下文
	 * 
	 */
	public static void installation(String appCode,String developerCode,
			String devLang,String fetchWay, String appName, String index, String describe, String fetchAddress) {
		DevSysConfigProp sysConfig = (DevSysConfigProp)JeancoderConfigurer.fetchDefault(PropType.SYS, "appcode");
		NamerApplication applicationConfigure = new NamerApplication();
		applicationConfigure.setAppCode(appCode);
		applicationConfigure.setDeveloperCode(developerCode);
		applicationConfigure.setFetchAddress(fetchAddress);
		applicationConfigure.setDevLang(DevLang.GROOVY);
		applicationConfigure.setFetchWay(FetchWay.MEMORY);
		applicationConfigure.setInstallWay(InstallWay.DISK);
		applicationConfigure.setInstallAddress(sysConfig.getLoadUri());
		applicationConfigure.setAppName(appName);
		applicationConfigure.setIndex(index);
		applicationConfigure.setDescribe(describe);
		
		//JCInterceptorStack.setNamerApplication(applicationConfigure);
		
		InstallerFactory.generateInstaller(applicationConfigure).install();
		Application application = ApplicationHolder.getInstance().getAppByCode(applicationConfigure.getAppCode());
		//注册拦截器
		Resource resource  = application.getResource(Common.INITIAL, Common.INITIAL);
		if (resource != null) {
			resource.getResult();
		}
		//打印资源树
//		ApplicationHolder.getInstance().prinAll(applicationConfigure.getAppCode());
		//初始应用相关的上下文
		JCApplicationContext jcApplicationContext = new JCApplicationContext();
		jcApplicationContext.setApplication(application);
		DatabasePowerHandler jcDatabasePower = (DatabasePowerHandler)PowerHandlerFactory.getPowerHandler(PowerName.DATABASE,appCode);
		jcApplicationContext.add(Common.DATABASE,(DatabasePower)jcDatabasePower);
		ApplicationContextPool.addApplicationContext(applicationConfigure.getAppCode(), jcApplicationContext);
		System.out.println("安装" + applicationConfigure.getAppCode() + "成功");
	}
	
	// 卸载
	public static void uninstall(String appCode) {
		ApplicationHolder.getInstance().removeApp(appCode);
		JCInterceptorStack.removeJCInterceptor(appCode);
		ApplicationContextPool.removeApplicationContext(appCode);
		LOGGER.info("spp:" + appCode + " uninstalled security success.");
	}
	
//	public static List<NamerApplicationDto> getApplicationAll() {
//		List<NamerApplication>  list = ApplicationHolder.getInstance().getAll();
//		List<NamerApplicationDto> dtoList = new ArrayList<NamerApplicationDto>();
//		for (NamerApplication item: list) {
//			NamerApplicationDto dto = new NamerApplicationDto();
//			dto.setAppCode(item.getAppCode());
//			dto.setAppName(item.getAppName());
//			dto.setDescribe(item.getDescribe());
//			dto.setIndex(item.getIndex());
//			dtoList.add(dto);
//		}
//		return dtoList;
//	}
	
	public static List<NamerApplicationDto> getApplicationAll() {
		VMDelegate wd = JCVMDelegatorGroup.instance().getDelegator();	
		ContainerMaps cm = wd.getVM().getContainers();
		List<NamerApplicationDto> dtoList = new ArrayList<NamerApplicationDto>();
		for (BCID item: cm.keySet()) {
			JCAppContainer container = cm.get(item);
			JCAPP app = container.getApp();
			NamerApplicationDto dto = new NamerApplicationDto();
			dto.setAppCode(app.getCode());
			dto.setAppName(app.getName());
			if(app.getConfig()!=null) {
				dto.setDescribe(app.getConfig().getDescription());
				if(app.getConfig().getIndex()!=null) {
					dto.setIndex(app.getConfig().getIndex());
				} else {
					dto.setIndex("welcome");
				}
			} else {
				dto.setIndex("welcome");
			}
			dtoList.add(dto);
		}
		return dtoList;
	}
	
	
	public static Enumeration<ShellServer>  getOnlineList() {
		return SlaveCli.instance().servers();
	}
}
