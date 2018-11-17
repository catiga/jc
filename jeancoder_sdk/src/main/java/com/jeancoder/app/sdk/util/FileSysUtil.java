package com.jeancoder.app.sdk.util;

import java.io.File;

public class FileSysUtil {
	 
	public static String getFileName(String filePath) { 
		int separatorInt = filePath.lastIndexOf(File.separator);
		String fileName = filePath.substring(separatorInt + 1);
		int dotInd = fileName.lastIndexOf(".");
		return fileName.substring(0,dotInd);
	} 
	
	public static String getFilePath(String filePath) { 
		int separatorInt = filePath.lastIndexOf(File.separator);
		return filePath.substring(0, separatorInt);
	} 
 
}
