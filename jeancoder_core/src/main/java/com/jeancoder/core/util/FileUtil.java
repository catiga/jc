package com.jeancoder.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.jeancoder.core.common.Common;

public class FileUtil {
	
	private static String ZIP = "504B0304";
	
	public static String getFirstDirPath(String source) {
		return getFirstDirPath(new File(source));
	}
	
	public static String getFirstDirPath(File source) {
		for(File f : source.listFiles()) {
			if(f.isDirectory()) {
				return f.getPath();
			}
		}
		return null;
	}
	
	public static File findFileFromPath(String source,String target) {
		return findFileFromPath(new File(source),target);
	}
	
	public static File findFileFromPath(File source,String target) {
		for(File f : source.listFiles()) {
			if(f.isDirectory() && f.getPath().endsWith(File.separator+target)) {
				return f;
			}
		}
		return null;
	}
	
	public static String getPostfix(File source) {
		return getPostfix(source.getName());
	}
	
	public static String getPostfix(String fileName) {
		int dotInd = fileName.lastIndexOf(".");
		if(dotInd != -1) {
			return fileName.substring(dotInd+1);
		}
		return null;
	}
	
	public static String cleanPostfix(String fileName) {
		int dotInd = fileName.lastIndexOf(".");
		if(dotInd != -1) {
			return fileName.substring(0,dotInd);
		}
		return fileName;
	}
	
	public static boolean isGroovyFile(File source) {
		return "groovy".equals(getPostfix(source));
	}
	
	public static boolean isGroovyFile(String filePath) {
		int separatorInt = filePath.lastIndexOf(File.separator);
		if (filePath.substring(separatorInt + 1).indexOf(".") == 0){
			return false;
		}
		return "groovy".equals(getPostfix(filePath));
	}
	
	public static String pathsJoint(String... parts) {
		String target = parts[0];
		for(int i=1,ic=parts.length;i<ic;i++) {
			target = pathJoint(target,parts[i]);
		}
		return target;
	}
	
	public static String pathJoint(String source,String part) {
		if(source.endsWith("/") && part.startsWith("/")) {
			return source + part.substring(1);
		}else if(source.endsWith("/") || part.startsWith("/")) {
			return source + part;
		}else {
			return source + "/" + part;
		}
	}
	
	public static boolean isZip(String filePath) { 
		byte[] b = new byte[4]; 
		try {
			FileInputStream is = new FileInputStream(filePath); 
			is.read(b, 0, b.length);
			String format = bytesToHexString(b);
			is.close();
			return ZIP.equals(format);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return false;
	  } 
	 
	private static String bytesToHexString(byte[] src) { 
		StringBuilder builder = new StringBuilder(); 
		if (src == null || src.length <= 0) { 
		  return null; 
		} 
		String hv; 
		for (int i = 0; i < src.length; i++) { 
			// 以十六进制（基数 16）无符号整数形式返回一个整数参数的字符串表示形式，并转换为大写 
			hv = Integer.toHexString(src[i] & 0xFF).toUpperCase(); 
			if (hv.length() < 2) { 
				builder.append(0); 
			} 
			builder.append(hv); 
		} 
		return builder.toString(); 
	} 
	
	
	/**
	 * 按路径拼接资源id  没有后缀名
	 * @param organizedPath
	 * @param filePath
	 * @return
	 */
	public static String getResid(final String organizedPath, final String filePath) { 
		int separatorInt = filePath.lastIndexOf(organizedPath);
		String fileName = filePath.substring(separatorInt + organizedPath.length());
		int dotInd = fileName.lastIndexOf(".");
		String file = fileName.substring(0, dotInd);
		// 注如果是windos 系统 需要把文件路径中的 \ 转换成/ 以方便网络能访问到 因为\ 前面有一个转移字符
		if (isWindows()) {
			file = file.replaceAll("\\\\", "/");
		}
		return file;
	} 
	
	/**
	 * 按路径拼接资源id 有后缀名
	 * @param organizedPath
	 * @param filePath
	 * @return
	 */
	public static String getResidSuffix(final String organizedPath, final String filePath) { 
		int separatorInt = filePath.lastIndexOf(organizedPath);
		String filePath_new  = filePath.substring(separatorInt + organizedPath.length());
		// 注如果是windos 系统 需要把文件路径中的 \ 转换成/ 以方便网络能访问到s
		if (isWindows()) {
			filePath_new = filePath_new.replaceAll("\\\\", "/");
		}
		return filePath_new;
	} 
	
	/**
	 * 根据文件路径 得到后缀名
	 * @param filePath
	 * @return
	 */
	public static String getSuffixName(String fileName) { 
		int dotInd = fileName.lastIndexOf(".");
		if (dotInd < 0) {
			return "";
		}
		return fileName.substring(dotInd + 1, fileName.length());
	} 
	
	public static void  deletefile(File file) {
		if (!file.exists()) {
			return;
		}
		if (file.listFiles() == null || file.listFiles().length == 0) {
			file.delete();
			return;
		}
		for (File f : file.listFiles()) {
			if (f.isDirectory()) {
				deletefile(f);
				f.delete();
			} else {
				f.delete();
			}
			file.delete();
		}
	} 
	
	public static String  getClassName(String fileNmae) {
		 String[] filennames = fileNmae.split(Common.MAVEN_STANDARD_SOURCE_CODE_PATH + File.separator);
		 String className =  cleanPostfix(filennames[1]);
		 className = className.replaceAll("/", ".");
		 return className;
	} 
	
	private static boolean isWindows() {
		String os = System.getProperty("os.name");
		if (os.indexOf("windows") >= 0 || os.indexOf("Windows") >= 0) {
			return true;
		}	
		return false;
	}
	
	/**
	 * 本方法给sdk 使用
	 * 因为渲染模板引擎的原因 因此对entryPath路径进行一次处理
	 * @param filePath
	 * @return
	 */
	public static String getResourcePath(final String entryPath, final String appPath) {
		String appPath_new  = appPath + File.separator;
		if (isWindows()) {
			appPath_new = appPath_new.replaceAll("\\\\", "\\\\\\\\");
		}
		return entryPath.replaceAll(appPath_new, "");
	}
	
	public static void main(String[] agr) {
		deletefile(new File("/Users/huangjie/workspace/server_trun"));
//		System.out.println(getResourcePath(""));
		//System.out.println(getSuffixName("1/.2"));
//		System.out.println(System.getProperty("os.name"));
		//System.out.println(getClassName("a/src/main/java/com/jeancoder/jeancoder_server/ready/dto/DatabaseConfigDto.groovy"));
	}
}
