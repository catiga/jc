package com.jeancoder.app.sdk.source.jeancoder;

import com.jeancoder.app.sdk.configure.DevSysConfigProp;
import com.jeancoder.core.cl.AppLoader;
import com.jeancoder.core.configure.JeancoderConfigurer;
import com.jeancoder.core.configure.PropType;

/**
 * ApplicationSource 只有指定的jeancoder 开发应用才能使用
 * 
 * @author huangjie
 *
 */
public class SysSource {
	public static AppLoader classLoader;
	
	private static ThreadLocal<AppLoader> APP_CLASS_LOADERS = new ThreadLocal<AppLoader>();
	
	/**
	 * 中央服务器下载地址 appcode sdk 中默认的appcode
	 * 
	 * @return
	 */
	public static String getDownloadURI() {
		DevSysConfigProp sys = (DevSysConfigProp) JeancoderConfigurer.fetchDefault(PropType.SYS, "appcode");
		if (sys == null) {
			return null;
		}
		return sys.getDownloadUri();
	}

	/**
	 * 下载解压存放地址
	 * 
	 * @return
	 */
	public static String getLoadURI() {
		DevSysConfigProp sys = (DevSysConfigProp) JeancoderConfigurer.fetchDefault(PropType.SYS, "appcode");
		if (sys == null) {
			return null;
		}
		return sys.getLoadUri();
	}

	/**
	 * 当前应用上下文对应的脚本
	 * 
	 * @return
	 */
	public static AppLoader getClassLoader() {
		return APP_CLASS_LOADERS.get();
	}

	public static void setClassLoader(AppLoader cc) {
		APP_CLASS_LOADERS.set(cc);
	}
	
	public static void clearClassLoader() {
		APP_CLASS_LOADERS.remove();
	}

	public static boolean isRootPrject() {
		return false;
	}

	/**
	 * svn 检出跟目录
	 * 
	 * @return
	 */
	public static String getCheckURI() {
		DevSysConfigProp sys = (DevSysConfigProp) JeancoderConfigurer.fetchDefault(PropType.SYS, "appcode");
		if (sys == null) {
			return null;
		}
		return sys.getCheckUri();
	}

	/**
	 * zip 根目录
	 * 
	 * @return
	 */
	public static String getZipURI() {
		DevSysConfigProp sys = (DevSysConfigProp) JeancoderConfigurer.fetchDefault(PropType.SYS, "appcode");
		if (sys == null) {
			return null;
		}
		return sys.getZipUri();
	}

	public static void setDefaultIndexPath(String defaultIndexPath) {
	}
}