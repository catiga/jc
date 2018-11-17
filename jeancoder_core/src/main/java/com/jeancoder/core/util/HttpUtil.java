package com.jeancoder.core.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class HttpUtil {
	public final static String AJAX = "XMLHttpRequest";
	public final static String CSS = "text/css;charset=UTF-8";
	public final static String JS = "text/js;charset=UTF-8";
	public final static String HTML = "text/html;charset=UTF-8";
	public final static String JSON = "application/json";
	public final static String JPEG = "image/jpeg";
	public final static String PNG = "image/png";
	public final static String ETO = "font";
	public final static String  WOFF = "font";
	public final static String  TTF = "font";
	public final static String  GIF = "image/gif";
	
	/**
	 * 后缀类型
	 */
	public final static String SUFFIX_CSS = "css";
	public final static String SUFFIX_JS = "js";
	public final static String SUFFIX_JPEG = "jpeg";
	public final static String SUFFIX_JPG = "jpg";
	public final static String SUFFIX_ETO = "eto";
	public final static String SUFFIX_WOFF = "woff";
	public final static String SUFFIX_TTF = "ttf";
	public final static String SUFFIX_GIF = "gif";
	public final static String SUFFIX_PNG = "png";
	private static Map<String, String> map = new HashMap<String, String>();
	static {
		map.put(SUFFIX_CSS, CSS);
		map.put(SUFFIX_JS, JS);
		map.put(SUFFIX_JPEG, JPEG);
		map.put(SUFFIX_JPG, JPEG);
		map.put(SUFFIX_GIF, GIF);
		map.put(SUFFIX_PNG, PNG);
//		map.put(SUFFIX_ETO, ETO);
//		map.put(SUFFIX_WOFF, WOFF);
//		map.put(SUFFIX_WOFF, WOFF);
		
	}
	/**
	 * 根据文件名称 得到对应的ContentType
	 * @param path
	 * @return
	 * YDL 6/20 
	 */
	public static String getContentType(String fileName) {
		String  suffix = FileUtil.getSuffixName(fileName);
		String contentType = map.get(suffix);
		if (StringUtil.isEmpty(contentType)) {
			return HTML;
		} else {
			return contentType;
		}
		
	}
	
	public static String getContentType(HttpServletRequest request) {
		return "";
	}
	
	
	public  static void main(String[] arg) {
		System.out.println(getContentType("1/2/2.3.4"));
	}
}
