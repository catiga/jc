package com.jeancoder.cap.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 正则表达式字典
 * @author wow zhang_gh@cpis.cn
 * @date 2018年6月13日
 */
public class DevPropRegDict {
	
	private static List<DevPropParser> parsers = new ArrayList<DevPropParser>();
	static {
		//关系型数据库
		parsers.add(new DevDatabasePropParser("database.([^.]*).default") {
			@Override
			public void setValue(Object instance, Properties p, String pkey) throws Exception {
				DevDatabaseProp.class.getMethod("setIsDefault",Boolean.class).invoke(instance, true);
			}
		});
		parsers.add(new DevDatabasePropParser("database.([^.]*).jdbc.driver") {
			@Override
			public void setValue(Object instance, Properties p, String pkey) throws Exception {
				DevDatabaseProp.class.getMethod("setDriveClass",String.class).invoke(instance, p.getProperty(pkey));
			}
		});
		parsers.add(new DevDatabasePropParser("database.([^.]*).jdbc.url") {
			@Override
			public void setValue(Object instance, Properties p, String pkey) throws Exception {
				DevDatabaseProp.class.getMethod("setUrl",String.class).invoke(instance, p.getProperty(pkey));
			}
		});
		parsers.add(new DevDatabasePropParser("database.([^.]*).jdbc.username") {
			@Override
			public void setValue(Object instance, Properties p, String pkey) throws Exception {
				DevDatabaseProp.class.getMethod("setUser",String.class).invoke(instance, p.getProperty(pkey));
			}
		});
		parsers.add(new DevDatabasePropParser("database.([^.]*).jdbc.password") {
			@Override
			public void setValue(Object instance, Properties p, String pkey) throws Exception {
				DevDatabaseProp.class.getMethod("setPassword",String.class).invoke(instance, p.getProperty(pkey));
			}
		});
		//关系型数据库
		
		
		//应用信息配置
		parsers.add(new DevSystemPropParser("application.([^.]*).code") {
			@Override
			public void setValue(Object instance, Properties p, String pkey) throws Exception {
				DevSystemProp.class.getMethod("setCode", String.class).invoke(instance, p.getProperty(pkey));
			}
		});
		parsers.add(new DevSystemPropParser("application.([^.]*).appid") {
			@Override
			public void setValue(Object instance, Properties p, String pkey) throws Exception {
				DevSystemProp.class.getMethod("setAppid", String.class).invoke(instance, p.getProperty(pkey));
			}
		});
		parsers.add(new DevSystemPropParser("application.([^.]*).name") {
			@Override
			public void setValue(Object instance, Properties p, String pkey) throws Exception {
				DevSystemProp.class.getMethod("setName", String.class).invoke(instance, p.getProperty(pkey));
			}
		});
		parsers.add(new DevSystemPropParser("application.([^.]*).developercode") {
			@Override
			public void setValue(Object instance, Properties p, String pkey) throws Exception {
				DevSystemProp.class.getMethod("setDevelopercode", String.class).invoke(instance, p.getProperty(pkey));
			}
		});
		parsers.add(new DevSystemPropParser("application.([^.]*).describe") {
			@Override
			public void setValue(Object instance, Properties p, String pkey) throws Exception {
				DevSystemProp.class.getMethod("setDescribe", String.class).invoke(instance, p.getProperty(pkey));
			}
		});
		parsers.add(new DevSystemPropParser("application.([^.]*).index") {
			@Override
			public void setValue(Object instance, Properties p, String pkey) throws Exception {
				DevSystemProp.class.getMethod("setIndex", String.class).invoke(instance, p.getProperty(pkey));
			}
		});
		//应用信息配置
		
		
		//本地多应用开发配置
		parsers.add(new DevCommunicationPropParser("namer.([^.]*).domain") {
			@Override
			public void setValue(Object instance, Properties p, String pkey) throws Exception {
				DevCommunicationProp.class.getMethod("setDomain", String.class).invoke(instance, p.getProperty(pkey));
			}
		});
		parsers.add(new DevCommunicationPropParser("namer.([^.]*).deploy") {
			@Override
			public void setValue(Object instance, Properties p, String pkey) throws Exception {
				DevCommunicationProp.class.getMethod("setDeploy", Integer.class).invoke(instance, Integer.valueOf(p.getProperty(pkey)));
			}
		});
		//本地多应用开发配置
		
		
		//系统配置路径获取
		//获取源代码检出保存路径
		parsers.add(new DevSysConfigPropParser("sys.([^.]*).check") {
			@Override
			public void setValue(Object instance, Properties p, String pkey) throws Exception {
				DevSysConfigProp.class.getMethod("setCheckUri", String.class).invoke(instance, p.getProperty(pkey));
			}
		});
		
		//获取项目代码打包保存路径
		parsers.add(new DevSysConfigPropParser("sys.([^.]*).zip") {
			@Override
			public void setValue(Object instance, Properties p, String pkey) throws Exception {
				DevSysConfigProp.class.getMethod("setZipUri", String.class).invoke(instance, p.getProperty(pkey));
			}
		});
		
		//获取项目包 下载保存路径
		parsers.add(new DevSysConfigPropParser("sys.([^.]*).load") {
			@Override
			public void setValue(Object instance, Properties p, String pkey) throws Exception {
				DevSysConfigProp.class.getMethod("setLoadUri", String.class).invoke(instance, p.getProperty(pkey));
			}
		});
		
		//获取中央服务下载地址
		parsers.add(new DevSysConfigPropParser("sys.([^.]*).download") {
			@Override
			public void setValue(Object instance, Properties p, String pkey) throws Exception {
				DevSysConfigProp.class.getMethod("setDownloadUri", String.class).invoke(instance, p.getProperty(pkey));
			}
		});
		
		
		//七牛配置
		parsers.add(new DevQiniuPropParser("qiniu.([^.]*).access") {
			@Override
			public void setValue(Object instance, Properties p, String pkey) throws Exception {
				DevQiniuProp.class.getMethod("setAccess", String.class).invoke(instance, p.getProperty(pkey));
			}
		});
		parsers.add(new DevQiniuPropParser("qiniu.([^.]*).secret") {
			@Override
			public void setValue(Object instance, Properties p, String pkey) throws Exception {
				DevQiniuProp.class.getMethod("setSecret", String.class).invoke(instance, p.getProperty(pkey));
			}
		});
		parsers.add(new DevQiniuPropParser("qiniu.([^.]*).defaultBucke") {
			@Override
			public void setValue(Object instance, Properties p, String pkey) throws Exception {
				DevQiniuProp.class.getMethod("setDefaultBucke", String.class).invoke(instance, p.getProperty(pkey));
			}
		});
		
		
		//缓存配置
		parsers.add(new DevMemPropParser("mem.([^.]*).server") {
			@Override
			public void setValue(Object instance, Properties p, String pkey) throws Exception {
				DevMemProp.class.getMethod("setServer", String.class).invoke(instance, p.getProperty(pkey));
			}
		});
		
	}
	
	public static DevPropParser getParse(String pkey) {
		for(DevPropParser parser : parsers) {
			if(parser.isMatcher(pkey)) {
				return parser;
			}
		}
		return null;
	}
}
