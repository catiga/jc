package com.jeancoder.core.common;

import java.io.File;

public class Common {
	public final static String GROOVY = "groovy";
	public final static String VIEW =  "template";
	public final static String READY =  "ready";
	public final static String ENTRY =  "entry";
	public final static String STATIC =  "static";
	public final static String INTERNAL =  "internal";
	public final static String INTERCEPTOR =  "interceptor";
	public final static String INITIAL =  "InitialConfiguration";
	public final static String DUMP = "DumpConfiguration";
	public final static String MAVEN_STANDARD_SOURCE_CODE_PATH = "src" + File.separator + "main" + File.separator + "java";
	public final static String  COM = "com";
	// 解析资源 路径需要用到的
	//    READY_PATH  ENTRY_PATH这三个路径需要和组织路径一起使用
	public final static String READY_PATH = File.separator + READY + File.separator;
	public final static String ENTRY_PATH = File.separator + ENTRY + File.separator;
	public final static String INTERNAL_PATH = File.separator + INTERNAL + File.separator;
	public final static String INTERCEPTOR_PATH = File.separator + INTERCEPTOR + File.separator;
	public final static String INITIAL_PATH = File.separator + INITIAL + "." + GROOVY;
	public final static String DUMP_PATH = File.separator + DUMP + "." + GROOVY;
	
	// 视图资源资源路径 img
	public final static String VIEW_PATH =  File.separator + VIEW + File.separator;
	//静态文件 资源路径 css  js
	public final static String STATIC_PATH = File.separator + STATIC + File.separator;
	
	
	// 应用上下文中使用
	public final static String REQUEST = "request";
	public final static String RESPONSE = "response";
	public final static String DATABASE = "database";
	public final static String CLASS_LOADER = "classLoader";
	public final static String MEM_POWER = "MemPower";
	public final static String QINIU_POWER = "QiniuPower";
	
}
