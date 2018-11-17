
package com.jeancoder.core.namer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.jeancoder.core.common.Common;
import com.jeancoder.core.exception.AppLoadException;
import com.jeancoder.core.util.FileUtil;

/**
 * 内存载入器 ZipInputStream流，需要先解压到本地磁盘，然后将静态文件留在磁盘上,其他文件载入到内存中 文件夹路径,
 * 静态文件留在本地,其他文件载入到内存中
 * 
 * @author huangjie
 */
public class MemoryNamerLoad extends NamerLoad {

	@Override
	public String loadContext(NamerApplication namerApplication, IFetchResult fetchResult) {
		try{
			String organization = Common.COM + File.separator + namerApplication.getDeveloperCode() + File.separator + namerApplication.getAppCode();
			if (fetchResult instanceof ZipInputStreamFetchResult) {
				String loadPath = isZipInputStreamLoadContext(namerApplication, organization, (ZipInputStreamFetchResult)fetchResult);
				return  new File(loadPath).toURI().toString();
			}
			if (fetchResult instanceof FilePathFetchResult) {
				isFilePathLoadContext(namerApplication, new File(new URI((String)fetchResult.getFetchResult())));
				return (String)fetchResult.getFetchResult();
			}
			return null;
		} catch (Exception e) {
			throw new AppLoadException("", e);
		}
	}

	private String isZipInputStreamLoadContext(NamerApplication application, String organization, ZipInputStreamFetchResult fetchResult) {
		String appPath = "";
		try {
			String sysLoadPath = application.getInstallAddress();
			File sysLoadfile = new File(sysLoadPath);
			if (!sysLoadfile.exists()) {
				sysLoadfile.mkdir();
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
				//除ready包下的groovy 脚本片段 都载入到内存中
				if (!entry.isDirectory() && FileUtil.isGroovyFile(file.getPath()) && file.getPath().indexOf(Common.MAVEN_STANDARD_SOURCE_CODE_PATH) >= 0
						&& file.getPath().indexOf(organization + Common.READY_PATH) < 0) {
					StringBuffer html = new StringBuffer();  
					BufferedReader br = new BufferedReader(new InputStreamReader(zis, Charset.forName("UTF-8")));
			        String line;  
			        while ((line = br.readLine()) != null) {
			        	html.append(line+ "\r\n");  
			        }  
			        MemoryPool.addResource(application.getAppCode(), file.getPath(), html.toString());
			        continue;
				}
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
	
	/**
	 * 只需要循路径下的文件，把指定的grrovy加载到内存中
	 * @param application
	 * @param fetchResult
	 * @return
	 */
	private void isFilePathLoadContext(NamerApplication application, File file) {
		try {
			if (!file.isDirectory() && FileUtil.isGroovyFile(file.getPath()) && file.getPath().indexOf(Common.MAVEN_STANDARD_SOURCE_CODE_PATH) >= 0) {
				StringBuffer html = new StringBuffer();  
				BufferedReader br = new BufferedReader(new FileReader(file));
		        String line;  
		        while ((line = br.readLine()) != null) {
		        	html.append(line+ "\r\n");  
		        }  
		        MemoryPool.addResource(application.getAppCode(), file.getPath(), html.toString());
		        br.close();
		        return;
			}
			if (!file.isDirectory()) {
				return;
			}
			for (File f : file.listFiles()) {
				isFilePathLoadContext(application, f);
			}
		} catch (Exception e) {
			throw new AppLoadException("load failure", e);
		}
	}
}
