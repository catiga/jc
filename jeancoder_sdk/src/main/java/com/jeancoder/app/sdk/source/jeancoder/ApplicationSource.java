package com.jeancoder.app.sdk.source.jeancoder;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import com.jc.channel.SlaveCli;
import com.jc.proto.conf.AppMod;
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
import com.jeancoder.core.util.JackSonBeanMapper;
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
	@Deprecated
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
	@Deprecated
	public static void uninstall(String appCode) {
		ApplicationHolder.getInstance().removeApp(appCode);
		JCInterceptorStack.removeJCInterceptor(appCode);
		ApplicationContextPool.removeApplicationContext(appCode);
		LOGGER.info("spp:" + appCode + " uninstalled security success.");
	}
	
	
	
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

	public static  List<String> getOnlineList() {
		return SlaveCli.instance().slaveServers();
	}
	
	public static Enumeration<ShellServer>  getSlaveServers() {
		return SlaveCli.instance().localServers();
	}
	
	public static void upgradeApp(String client_instance_id, String modJson) {
		AppMod appmode = JackSonBeanMapper.fromJson(modJson, AppMod.class);
		SlaveCli.instance().upgradeApp(client_instance_id, appmode);
	}
	
	public static void installApp(String client_instance_id, String modJson) {
		AppMod appmode = JackSonBeanMapper.fromJson(modJson, AppMod.class);
		SlaveCli.instance().installApp(client_instance_id, appmode);
	}
	
	public static  void uninstallApp(String client_instance_id, String modJson) {
		AppMod appmode = JackSonBeanMapper.fromJson(modJson, AppMod.class);
		SlaveCli.instance().uninstallApp(client_instance_id, appmode);
	}
	
	/******************************** New Method Start **********************************/
	/**
	 * client_instance 实际存储的是实例对象的数据模型
	 * keys:
	 * 	instance_id : Long
	 *  instance_ver: String
	 *  instance_name:String
	 *     
	 * @param client_instance
	 * @param modJson
	 */
	public static void upgradeAppRich(Map<String, Object> client_instance, String modJson) {
		String client_instance_id = client_instance.get("instance_id") + "";
		
		AppMod appmode = JackSonBeanMapper.fromJson(modJson, AppMod.class);
		SlaveCli.instance().upgradeApp(client_instance_id, appmode);
	}
	
	public static void installAppRich(Map<String, Object> client_instance, String modJson) {
		String client_instance_id = client_instance.get("instance_id") + "";
		
		AppMod appmode = JackSonBeanMapper.fromJson(modJson, AppMod.class);
		SlaveCli.instance().installApp(client_instance_id, appmode);
	}
	
	public static  void uninstallAppRich(Map<String, Object> client_instance, String modJson) {
		String client_instance_id = client_instance.get("instance_id") + "";
		
		AppMod appmode = JackSonBeanMapper.fromJson(modJson, AppMod.class);
		SlaveCli.instance().uninstallApp(client_instance_id, appmode);
	}
	
}
