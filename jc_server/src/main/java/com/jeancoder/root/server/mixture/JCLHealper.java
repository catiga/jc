package com.jeancoder.root.server.mixture;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JCLHealper {
	private final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCLNbmKl9/gLn7Bef/xtUkshC1WyrLZLRpXCcFYR1gQi0isWsZBTicC4efBOkkNG3r+1ue0gvtuU/tjREFGf4Y7HaKHGb5tNCOlMNeNjM5YLRwLFqrUSsQyD4rj4eua1ltearr24R0HilnTvnQm6Z/UY0s21vdOUFQBPY0GNAa+0wIDAQAB";
	
	private static Logger logger = LoggerFactory.getLogger(JCLHealper.class);
	
	public static final JCLHealper INSTENSE = new JCLHealper();
	
	public String getPub_key_file() {
		return pub_key_file;
	}

	private JCLHealper() {
		//读取证书文件
		StringBuffer buff = new StringBuffer();
		try {
			// 本地读取配置文件
			InputStream ins = JCLHealper.class.getClassLoader().getResourceAsStream("com/jeancoder/root/server/key/jc.pub");
			BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
			String lineContent = null;
			while ((lineContent = reader.readLine()) != null) {
				buff.append(lineContent);
			}
		} catch (Exception e) {
		}
		this.pub_key_file = buff.toString();
		
		//读取license文件
		buff = new StringBuffer();
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
		this.license = buff.toString();
	}
	
	private String pub_key_file = null;
	
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
		String default_pub_key = publicKey;
		if(pub_key_file!=null&&!pub_key_file.equals("")) {
			default_pub_key = pub_key_file;
		}
		try {
			RSA.decryptByPublic(sourceText, default_pub_key);
			return true;
		}catch(Exception e) {
			return false;
		}
	}
	
	public  String encryptByPublic(String sourceText) {
		String default_pub_key = publicKey;
		if(pub_key_file!=null&&!pub_key_file.equals("")) {
			default_pub_key = pub_key_file;
		}
		return RSA.encryptByPublic(sourceText, default_pub_key);
	}
	
	public  String decryptByPublic(String sourceText) {
		String default_pub_key = publicKey;
		if(pub_key_file!=null&&!pub_key_file.equals("")) {
			default_pub_key = pub_key_file;
		}
		logger.info("source_text:" + sourceText);
		logger.info("decrypt pub key:" + this.pub_key_file);
		return RSA.decryptByPublic(sourceText, default_pub_key);
	}
	
	public  String getMerchantsCode() {
		String license = decryptByPublic(JCLHealper.INSTENSE.getLicense());
		return license.split("/")[1];
	}
	
	public  String getInstanceNum() {
		String license = decryptByPublic(JCLHealper.INSTENSE.getLicense());
		return license.split("/")[2];
	}

	public String getLicense() {
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
		String license = JCLHealper.INSTENSE.getLicense();
		String pub_key = JCLHealper.INSTENSE.getPub_key_file();
		
		System.out.println(license);
		System.out.println(pub_key);
		
		System.out.println(JCLHealper.INSTENSE.getMerchantsCode());
		
		String lic = "GvaRZSVEHP1N6omCUFuGsXz2vFNprc9aQW1ah0FPnr/Hj5goVlugGdkl70e/4hYUevIys/bjqHmgoyhAEkbCagqtK9mdwaDXSsNj0cafojCSNOmRvHr+gghYelmI8cxTsjpghjOfrGtRv1FmDtooegXbgooFP8p91ugdTFDB+8E=";
		
		String pb = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCSdHxp9fi6oF86fzV7Pt2Mel1Yiy2UN2CVUUfpXCGwk9f/TrFEme5KW8gxqNSABx1h/FdhRUP4bsFCc4bsArrl3pdsnVws0I1DwpuZRiw4stLLOxD4JS1RBT+vVghuvHRp7LRB/9e8T/o/RUiw1hbLmYz62En3cnmqMKsiqM7uDwIDAQAB";
		
		String s = RSA.decryptByPublic(lic, pb);
		System.out.println(s);
	}
	
}
