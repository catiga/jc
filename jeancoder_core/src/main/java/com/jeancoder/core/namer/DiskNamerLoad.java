package com.jeancoder.core.namer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.jeancoder.core.exception.AppLoadException;
import com.jeancoder.core.util.FileUtil;

/**
 *  磁盘载入器
 *  如果得到的获取结果 是文件路径 怎返回不处理，
 *  如何得到的是zip包怎解压到指定的目录
 *  最后返回的是app所在的路径
 * @author huangjie
 *
 */
public class DiskNamerLoad extends NamerLoad {

	@Override
	public String loadContext(NamerApplication appContext, IFetchResult fetchResult) { 
		try{
			if (fetchResult instanceof ZipInputStreamFetchResult) {
				String loadPath = isZipInputStreamLoadContext(appContext, (ZipInputStreamFetchResult)fetchResult);
				return new File(loadPath).toURI().toString();
			}
			if (fetchResult instanceof FilePathFetchResult) {
				return (String)fetchResult.getFetchResult();
			}
			return null;
		} catch (Exception e) {
			throw new AppLoadException("", e);
		}
	}
	
	/**
	 * 获取结果是ZipInputStream的, 磁盘载入器
	 * @param fetchResult
	 * @return
	 */
	private String isZipInputStreamLoadContext(NamerApplication appContext, ZipInputStreamFetchResult fetchResult) {
		String appPath = "";
		try {
			String sysLoadPath = appContext.getInstallAddress();
			File sysLoadfile = new File(sysLoadPath);
			if (!sysLoadfile.exists()) {
				sysLoadfile.mkdirs();
			}
			ZipInputStream zis = (ZipInputStream) fetchResult.getFetchResult();
			ZipEntry entry = zis.getNextEntry();
			appPath = sysLoadPath + File.separator + entry.getName();
			File appFile = new File(appPath);
			if (appFile.exists()) {
				FileUtil.deletefile(appFile);
			}
			appFile.mkdir();
			while (((entry = zis.getNextEntry()) != null)) {
				File file = new File(sysLoadPath + File.separator + entry.getName());
				if (entry.isDirectory()) {
					file.mkdirs();
				} else {
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					file.createNewFile();
					OutputStream out = new FileOutputStream(file);
					int len;
					byte[] _byte = new byte[1024];
					while ((len = zis.read(_byte)) > 0) {
						out.write(_byte, 0, len);
					}
					out.close();
				}
			}
			return appPath;
		} catch (Exception e) {
			throw new AppLoadException("load failure", e);
		}
	}
}
