package com.jeancoder.core.util;

import java.io.InputStream;


/**
 * 临时存储 网络下载的zip包
 */
public class InputStreamUtil {
	
	private static InputStream inputStream;
	private static String  url = "";
	
	
	public static InputStream get(String url) {
		
		if (InputStreamUtil.url.equals(url)) {
			return inputStream;
		}
		return null;
	}
	
	public static void put(String url, InputStream inputStream) {
		InputStreamUtil.inputStream = inputStream;
		InputStreamUtil.url = url;
	}

	public static void close() {
		inputStream = null;
		url = "";
	}
}
