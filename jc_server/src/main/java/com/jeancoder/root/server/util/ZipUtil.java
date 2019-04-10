package com.jeancoder.root.server.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeancoder.core.util.FileUtil;

public class ZipUtil {
	
	private static Logger logger = LoggerFactory.getLogger(ZipUtil.class);
	
	public static void  unzip(String path, ZipInputStream inputStream) throws Exception {
		ZipEntry entry = inputStream.getNextEntry();
		String name = entry.getName();
		File appFile = new File(path);
		if (appFile.exists()) {
			FileUtil.deletefile(appFile);
		}
		appFile.mkdirs();
//		entry = inputStream.getNextEntry();
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
	}
	
}
