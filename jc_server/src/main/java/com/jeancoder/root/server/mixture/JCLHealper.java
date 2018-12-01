package com.jeancoder.root.server.mixture;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JCLHealper {
	private final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCLNbmKl9/gLn7Bef/xtUkshC1WyrLZLRpXCcFYR1gQi0isWsZBTicC4efBOkkNG3r+1ue0gvtuU/tjREFGf4Y7HaKHGb5tNCOlMNeNjM5YLRwLFqrUSsQyD4rj4eua1ltearr24R0HilnTvnQm6Z/UY0s21vdOUFQBPY0GNAa+0wIDAQAB";
	
	public static final JCLHealper INSTENSE = new JCLHealper();
	
	//private final static String LICENSE_PATH = "license.jc";
 
	//private static String signkey;
	
	private String license = null;
	
	private boolean loaded = false;
	private boolean foundkey = true;
	
 
//	public static String getKey() {
//		StringBuffer buff = new StringBuffer();
//		try {
//			// 本地读取配置文件
//			InputStream ins = JCLHealper.class.getClassLoader().getResourceAsStream("com/jeancoder/root/server/key/license.jc");
//			BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
//			String lineContent = null;
//			while ((lineContent = reader.readLine()) != null) {
//				buff.append(lineContent);
//			}
//		} catch (Exception e) {
//		}
//		return buff.toString();
//	}
//	
//	
//	private static void init() {
//		StringBuffer buff = new StringBuffer();
//		try {
//			// 本地读取配置文件
//			InputStream ins = JCLHealper.class.getClassLoader().getResourceAsStream("com/jeancoder/root/server/key/license.jc");
//			BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
//			String lineContent = null;
//			while ((lineContent = reader.readLine()) != null) {
//				buff.append(lineContent);
//			}
//		} catch (Exception e) {
//		}
//	}
	
	
	
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
	
	@SuppressWarnings("static-access")
	public  String getMerchantsCode() {
		String license = decryptByPublic(JCLHealper.INSTENSE.getLicense());
		return license.split("/")[1];
	}
	
	@SuppressWarnings("static-access")
	public  String getInstanceNum() {
		String license = decryptByPublic(JCLHealper.INSTENSE.getLicense());
		return license.split("/")[2];
	}

	private static String getLicense() {
		if (JCLHealper.INSTENSE.license == null)  {
			StringBuffer buff = new StringBuffer();
			try {
				// 本地读取配置文件
				InputStream ins = JCLHealper.class.getClassLoader().getResourceAsStream("com/jeancoder/root/server/key/license.jc");
				BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
				String lineContent = null;
				while ((lineContent = reader.readLine()) != null) {
					buff.append(lineContent);
				}
			} catch (Exception e) {
			}
			JCLHealper.INSTENSE.license  = buff.toString();
		}
		return JCLHealper.INSTENSE.license;
	}
	
	public static void main(String[] agr) {
//		File ifle = new File("/Users/huangjie/git/jc_parent/jc_server/target/classes/com/jeancoder/root/server/key/license");
//		System.out.println(getKey());
//		JCLHealper jch = new JCLHealper();
//		jch.decryptByPublic("aJj5MlHwMSMGvgFwQFjQ9opvYjt72PKqAJ2QCtUnIXUvE1HTGtDzpmhs2j3WYlNYWiU0ZbqeY8n5agdZUtjw3a7otV1APykAZ1V2Lk6Ha2V2qrZu5E17MUWd3NkSzeR03zFaFKZ434SDmHhRNzqXJlHe7SOFjU0XiGF4VHXmep0=");
	}
	
}
