package com.jeancoder.app.sdk.configure;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import com.jeancoder.core.configure.ConfigureReader;
import com.jeancoder.core.configure.JeancoderConfigurer;
import com.jeancoder.core.configure.PropItem;
import com.jeancoder.core.configure.Props;
import com.jeancoder.core.util.FileUtil;

/**
 * 配置加载器
 * @author wow zhang_gh@cpis.cn
 * @date 2018年6月13日
 */
public class DevConfigureReader extends ConfigureReader{
	//resources目录
	private static String MAVEN_STANDARD_SOURCE_CODE_PATH = "src"+File.separator+"main"+File.separator+"resources";
	//约定的配置文件
	private static String APPOINT_SYSTEM_CONFIG_FILE="application.properties";
	
	/**
	 * 从约定位置约定文件读取系统配置 并且载入到系统中
	 * @param appPath
	 */
	public static void init(String appPath, String appcode) {
		Props props = new Props();
		try {
			File file = new File(FileUtil.pathsJoint(appPath, MAVEN_STANDARD_SOURCE_CODE_PATH,APPOINT_SYSTEM_CONFIG_FILE));
			if(!file.exists()) {
				//TODO 打印WARNING日志
				System.out.println("The file \"application.properties\" not found!");
				return;
			}
			Properties p = new Properties();
			p.load(new InputStreamReader(new FileInputStream(file),"UTF-8"));
	
			//遍历application.properties所有配置
			for(Object o : p.keySet()) {
				String pkey = String.valueOf(o);
 				DevPropParser parser = DevPropRegDict.getParse(pkey);
				//如果能正确获取到对应处理类 并且正确获取id 便给配置对象设置属性
				if(parser != null) {
					String id = parser.getId(pkey);
					if(id != null) {
						PropItem existsInstance= props.getProp(parser.getType(), id);
						if(existsInstance == null) {
							existsInstance = parser.getInstance(id);
							props.addProp(existsInstance);
						}
						try {
							parser.setValue(existsInstance, p, pkey);
						}catch(Exception e) {
							//TODO 打印WARNING日志
							System.out.println(pkey+"的配置有问题"+""+e.getMessage()+""+e.getCause());
						}
					}
				}
			}
		} catch (Exception e) {
			//TODO 打印WARNING日志
			e.printStackTrace();
			System.out.println("配置初始化失败"+""+e.getMessage()+""+e.getCause());
		}
		//配置载入到系统中
		JeancoderConfigurer.register(props, appcode);
	}
}
