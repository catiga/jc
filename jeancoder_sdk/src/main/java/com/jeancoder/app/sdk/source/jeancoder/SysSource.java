package com.jeancoder.app.sdk.source.jeancoder;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import com.jeancoder.core.cl.AppLoader;
import com.jeancoder.root.container.ContainerContextEnv;
import com.jeancoder.root.container.JCAppContainer;

/**
 * ApplicationSource 只有指定的jeancoder 开发应用才能使用
 * 
 * @author huangjie
 *
 */
public class SysSource {
//	
//	/**
//	 * 中央服务器下载地址 appcode sdk 中默认的appcode
//	 * 
//	 * @return
//	 */
//	public static String getDownloadURI() {
//		DevSysConfigProp sys = (DevSysConfigProp) JeancoderConfigurer.fetchDefault(PropType.SYS, "appcode");
//		if (sys == null) {
//			return null;
//		}
//		return sys.getDownloadUri();
//	}

	/**
	 * 下载解压存放地址
	 * 
	 * @return
	 */
	public static String getLoadURI() {
		return  "";
		
	}

	/**
	 * 当前应用上下文对应的脚本
	 * 
	 * @return
	 */
	
	public static boolean isRootPrject() {
		return false;
	}

	/**
	 * svn 检出跟目录
	 * 
	 * @return
	 */
	public static String getCheckURI() {
//		DevSysConfigProp sys = (DevSysConfigProp) JeancoderConfigurer.fetchDefault(PropType.SYS, "appcode");
//		if (sys == null) {
//			return null;
//		}
//		return sys.getCheckUri();
		
		JCAppContainer container = ContainerContextEnv.getCurrentContainer();
		if(container==null) {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			if(loader instanceof AppLoader) {
				AppLoader app_loader = (AppLoader)loader;
				
				container = app_loader.getContextEnv();
			}
		}
		String srt = container.getConfig("application.properties");
		StringReader sr = new StringReader(srt);
		Properties properties = new Properties();
		try {
			properties.load(sr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return properties.getProperty("sys.uri.check");
	}

	/**
	 * zip 根目录
	 * 
	 * @return
	 */
	public static String getZipURI() {
		JCAppContainer container = ContainerContextEnv.getCurrentContainer();
		if(container==null) {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			if(loader instanceof AppLoader) {
				AppLoader app_loader = (AppLoader)loader;
				
				container = app_loader.getContextEnv();
			}
		}
		StringReader sr = new StringReader(container.getConfig("application.properties"));
		Properties properties = new Properties();
		try {
			properties.load(sr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return properties.getProperty("sys.uri.zip");
	}

	
	public static String getMavenHome() {
		JCAppContainer container = ContainerContextEnv.getCurrentContainer();
		if(container==null) {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			if(loader instanceof AppLoader) {
				AppLoader app_loader = (AppLoader)loader;
				
				container = app_loader.getContextEnv();
			}
		}
		StringReader sr = new StringReader(container.getConfig("application.properties"));
		Properties properties = new Properties();
		try {
			properties.load(sr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return properties.getProperty("sys.uri.mavenHome");
	}
	
	public static void setDefaultIndexPath(String defaultIndexPath) {
	}
}