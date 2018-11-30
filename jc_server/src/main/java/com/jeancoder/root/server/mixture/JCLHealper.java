package com.jeancoder.root.server.mixture;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class JCLHealper {
	private final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCLNbmKl9/gLn7Bef/xtUkshC1WyrLZLRpXCcFYR1gQi0isWsZBTicC4efBOkkNG3r+1ue0gvtuU/tjREFGf4Y7HaKHGb5tNCOlMNeNjM5YLRwLFqrUSsQyD4rj4eua1ltearr24R0HilnTvnQm6Z/UY0s21vdOUFQBPY0GNAa+0wIDAQAB";
	
	public static final JCLHealper INSTENSE = new JCLHealper();
	
	private static String OP_SYSTEM_USER_PATH = System.getProperty("user.home");
	private static String DEFAULT_PROJECT_WORK_DIR = File.separator+".jeancoder";
	private static String DEFAULT_PROJECT_WORK_PATH = OP_SYSTEM_USER_PATH + DEFAULT_PROJECT_WORK_DIR;
	private static String DETAULT_SYSTEM_WORK_DIR = File.separator+"system";
	private static String DEFAULT_SYSTEM_WORK_PATH = DEFAULT_PROJECT_WORK_PATH + DETAULT_SYSTEM_WORK_DIR;
	private static String DEFAULT_SYSTEM_PORT_PATH = DEFAULT_SYSTEM_WORK_PATH + File.separator + getPort();
	private static String DEFAULT_SYSTEM_LICENSE_FILE = File.separator+"license";
	private static String DEFAULT_SYSTEM_LICENSE_PATH = DEFAULT_SYSTEM_PORT_PATH+DEFAULT_SYSTEM_LICENSE_FILE;
	
	private static String signkey;
	
	static {
		init();
	}
	
	public static void init() {
		// 如果对应的端口下文件不存在就查看
		File systemDir = new File(DEFAULT_SYSTEM_LICENSE_PATH);
		if(!systemDir.exists()) {
			systemDir.mkdirs();
		}

		File licenseFile = new File(DEFAULT_SYSTEM_LICENSE_PATH);
		if(!licenseFile.exists()) {
			//需要导入License
		}else {
			try {
				StringBuffer licenseStr = new StringBuffer();
				BufferedReader licenser = new BufferedReader(new InputStreamReader(new FileInputStream(licenseFile)));
				String line = null;
				while((line = licenser.readLine()) != null) {
					licenseStr.append(line);
				}
				licenser.close();
				
				JCLHealper.INSTENSE.license = licenseStr.toString();
				JCLHealper.INSTENSE.loaded = true;
			}catch(Exception e) {
			}
		}
	}
	
	@SuppressWarnings("unused")
	private String key = publicKey;
	private String license = null;
	
	private boolean loaded = false;
	private boolean foundkey = true;
	
	public boolean isLoaded() {
		return loaded;
	}
	
	public boolean isFoundkey() {
		return foundkey;
	}
	
	public boolean checkLicenseText(String sourceText) {
		try {
			RSA.decryptByPublic(sourceText, publicKey);
			return true;
		}catch(Exception e) {
			return false;
		}
	}
	
	public  String encryptByPublic(String sourceText) {
		return RSA.encryptByPublic(sourceText, publicKey);
	}
	
	public  String decryptByPublic(String sourceText) {
		return RSA.decryptByPublic(sourceText, publicKey);
	}
	
	public  String getMerchantsCode() {
		String license = decryptByPublic(JCLHealper.INSTENSE.license);
		return license.split("/")[1];
	}
	
	public  String getInstanceNum() {
		String license = decryptByPublic(JCLHealper.INSTENSE.license);
		return license.split("/")[2];
	}

	
	public static String getSignkey() {
		return signkey;
	}

	public static void setSignkey(String signkey) {
		JCLHealper.signkey = signkey;
	}
	
	public static void main(String[] agr) {
		JCLHealper jch = new JCLHealper();
		jch.decryptByPublic("aJj5MlHwMSMGvgFwQFjQ9opvYjt72PKqAJ2QCtUnIXUvE1HTGtDzpmhs2j3WYlNYWiU0ZbqeY8n5agdZUtjw3a7otV1APykAZ1V2Lk6Ha2V2qrZu5E17MUWd3NkSzeR03zFaFKZ434SDmHhRNzqXJlHe7SOFjU0XiGF4VHXmep0=");
	}
	
	/**
	 * 返回启动端口
	 * @return
	 */
	private static String getPort() {
//		MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
//		Set<ObjectName> objectNames;
//		try {
//			objectNames = beanServer.queryNames(new ObjectName("*:type=Connector,*"), Query.match(Query.attr("protocol"), Query.value("org.apache.coyote.http11.Http11NioProtocol")));
//			return objectNames.iterator().next().getKeyProperty("port");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return "8000";
	}
}
