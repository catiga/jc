package com.jeancoder.root.server.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jc.proto.conf.AppMod;
import com.jeancoder.core.util.FileUtil;

public class ZipUtil {
	
	final public static String RUNNING_PATH = "rn";
	
	private static Logger logger = LoggerFactory.getLogger(ZipUtil.class);
	
	/**
	 * @param mod  为当前要安装的app配置信息对象
	 * @param inputStream
	 * @throws Exception
	 */
	public static String init_install(AppMod mod, ZipInputStream inputStream) throws Exception {
		String path = mod.getApp_base();
		ZipEntry entry = inputStream.getNextEntry();
		String name = entry.getName();
		File appFile = new File(path);
		if (appFile.exists()) {
			FileUtil.deletefile(appFile);
		}
		if(mod.getApp_ver()!=null) {
			path += "/" + mod.getApp_ver();
		}
		
		appFile = new File(path);
		appFile.mkdirs();
		while (((entry = inputStream.getNextEntry()) != null)) {
			String entryName = entry.getName();
			try {
				entryName = entryName.substring(name.length(),entryName.length());
			}catch(Exception e) {
				logger.error(entryName + " NOT FOUND, WILL BE IGNORED.");
				continue;
			}
			File file = new File(path + File.separator + entryName);
			if (entry.isDirectory()) {
				file.mkdirs();
			} else {
				if(!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				file.createNewFile();
				OutputStream out = new FileOutputStream(file);
				int len;
				byte[] _byte = new byte[1024];
				while ((len = inputStream.read(_byte)) > 0) {
					out.write(_byte, 0, len);
				}
				out.close();
			}
		}
		return path;
	}
	
	public static String re_install(AppMod mod, ZipInputStream inputStream) throws Exception {
		String path = mod.getApp_base();
		ZipEntry entry = inputStream.getNextEntry();
		String name = entry.getName();
		
		/*
		File appFile = new File(path);
		if (appFile.exists()) {
			FileUtil.deletefile(appFile);
		}
		*/
		
		if(mod.getApp_ver()!=null) {
			path += "/" + mod.getApp_ver();
		}
		//针对path加上当前时间戳
		path = path + "_" + System.currentTimeMillis();	//留作后期改名
		
		File appFile = new File(path);
		appFile.mkdirs();
		while (((entry = inputStream.getNextEntry()) != null)) {
			String entryName = entry.getName();
			try {
				entryName = entryName.substring(name.length(),entryName.length());
			}catch(Exception e) {
				logger.error(entryName + " NOT FOUND, WILL BE IGNORED.");
				continue;
			}
			File file = new File(path + File.separator + entryName);
			if (entry.isDirectory()) {
				file.mkdirs();
			} else {
				if(!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				file.createNewFile();
				OutputStream out = new FileOutputStream(file);
				int len;
				byte[] _byte = new byte[1024];
				while ((len = inputStream.read(_byte)) > 0) {
					out.write(_byte, 0, len);
				}
				out.close();
			}
		}
		return path;
	}
	
}
