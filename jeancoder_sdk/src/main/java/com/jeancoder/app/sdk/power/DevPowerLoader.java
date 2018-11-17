package com.jeancoder.app.sdk.power;

import java.util.List;

import com.jeancoder.app.sdk.configure.DevDatabaseProp;
import com.jeancoder.app.sdk.configure.DevMemProp;
import com.jeancoder.app.sdk.configure.DevQiniuProp;
import com.jeancoder.core.configure.JeancoderConfigurer;
import com.jeancoder.core.configure.PropItem;
import com.jeancoder.core.configure.PropType;
import com.jeancoder.core.exception.JeancoderException;
import com.jeancoder.core.power.DatabasePowerConfig;
import com.jeancoder.core.power.MemPowerConfig;
import com.jeancoder.core.power.PowerHandlerFactory;
import com.jeancoder.core.power.PowerName;
import com.jeancoder.core.power.QiniuPowerConfig;

public class DevPowerLoader {
	//TODO 暂时先这样写 可以考虑优化 系统能力分类如何 动态类型 动态添加 
	public static void init(String appcode) {
		List<PropItem> dbprops = JeancoderConfigurer.fetch(PropType.DATABASE, appcode);
		if(dbprops != null) {
			for(PropItem propItem : JeancoderConfigurer.fetch(PropType.DATABASE, appcode)) {
				DevDatabaseProp item = (DevDatabaseProp)propItem;
				try {
					PowerHandlerFactory.generatePowerHandler(PowerName.DATABASE, dbprop2dbconfig(item),appcode);
				} catch (JeancoderException e) {
					//TODO 打印WARNING日志
					System.out.println("Power generate failed which type is "+PowerName.DATABASE+" id is "+item.getId()+"."+e.getMessage()+e.getCause());
				}
			}
		}
		
		List<PropItem> qiniuprops = JeancoderConfigurer.fetch(PropType.QINIU, appcode);
		if(qiniuprops != null) {
			for(PropItem propItem : JeancoderConfigurer.fetch(PropType.QINIU, appcode)) {
				DevQiniuProp item = (DevQiniuProp)propItem;
				try {
					QiniuPowerConfig config = new QiniuPowerConfig();
					config.setAccessKey(item.getAccess());
					config.setSecretKey(item.getSecret());
					config.setDefaultBucket(item.getDefaultBucke());
					config.setDefault(item.getIsDefault());
					config.setId(item.getId());
					PowerHandlerFactory.generatePowerHandler(PowerName.QINIU, config, appcode);
				} catch (JeancoderException e) {
					//TODO 打印WARNING日志
					System.out.println("Power generate failed which type is "+PowerName.QINIU+" id is "+item.getId()+"."+e.getMessage()+e.getCause());
				}
			}
		}
		
		
		List<PropItem> memcacheprops = JeancoderConfigurer.fetch(PropType.MEM, appcode);
		if(memcacheprops != null) {
			for(PropItem propItem : JeancoderConfigurer.fetch(PropType.MEM, appcode)) {
				DevMemProp item = (DevMemProp)propItem;
				try {
					MemPowerConfig config = new MemPowerConfig();
					config.setDefault(item.getIsDefault());
					config.setServer(item.getServer());
					config.setId(item.getId());
					
					PowerHandlerFactory.generatePowerHandler(PowerName.MEM, config, appcode);
				} catch (JeancoderException e) {
					//TODO 打印WARNING日志
					System.out.println("Power generate failed which type is "+PowerName.MEM+" id is "+item.getId()+"."+e.getMessage()+e.getCause());
				}
			}
		}
	}
	
	private static DatabasePowerConfig dbprop2dbconfig(DevDatabaseProp item) {
		DatabasePowerConfig config = new DatabasePowerConfig();
		config.setDefault(item.getIsDefault());
		config.setId(item.getId());
		config.setDriveClass(item.getDriveClass());
		config.setUrl(item.getUrl());
		config.setUser(item.getUser());
		config.setPassword(item.getPassword());
		return config;
	}
}
