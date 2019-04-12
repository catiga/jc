package com.jeancoder.root.server.mixture;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JCLHealper {
	
	static Logger logger = LoggerFactory.getLogger(JCLHealper.class);
	
	private final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCLNbmKl9/gLn7Bef/xtUkshC1WyrLZLRpXCcFYR1gQi0isWsZBTicC4efBOkkNG3r+1ue0gvtuU/tjREFGf4Y7HaKHGb5tNCOlMNeNjM5YLRwLFqrUSsQyD4rj4eua1ltearr24R0HilnTvnQm6Z/UY0s21vdOUFQBPY0GNAa+0wIDAQAB";
	
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
		logger.info("pub_key_file=" + pub_key_file);
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
		logger.info("license=" + license);
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
//		String license = JCLHealper.INSTENSE.getLicense();
//		String pub_key = JCLHealper.INSTENSE.getPub_key_file();
//		
//		System.out.println(license);
//		System.out.println(pub_key);
//		
//		System.out.println(JCLHealper.INSTENSE.getMerchantsCode());
		
		String code = "v2.0.1/jcweb/0000/20200211/1529992261836";
		
		String pr = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIs1uYqX3+AufsF5//G1SSyELVbKstktGlcJwVhHWBCLSKxaxkFOJwLh58E6SQ0bev7W57SC+25T+2NEQUZ/hjsdoocZvm00I6Uw142MzlgtHAsWqtRKxDIPiuPh65rWW15quvbhHQeKWdO+dCbpn9RjSzbW905QVAE9jQY0Br7TAgMBAAECgYBcYhbzpr5no/Nyqmf0G/6nkEAWbQYrogbs5AhvcUk8EXL1DnirNhYlj42hafC4xhflrvCtlo8NNKaLxewbwN1uuzG8A2jd+ROEXlx5HDh2ZluhtHzL/SmNcJXo684xAl2pCNVBjDcW48PcIBijke/sTVHTDsDCukLKDPUOM/mKIQJBAL96k4+jBscazsJiuZ6C3RFDVtRRDpf1dMgLgxcx63bAXkA2Arau0J49IAYmSVJoDXqDoJKWdXJVh9vHSkhN/48CQQC6Hk1/G0Y0nOylf6NOp0oMgc0A+etnwxHKqwtctPKjEYcJx2fzALzTtCoySLYXX7gLnPIQXpQBTUysG5skBKp9AkEAiSQm6fqu0Q4fRlRlc+VwpnufhgPkOuw/z0OHiaZkajJPjxfgC63bl2paNG1ZmJ8UAEqkSDlhNxmRa9UqG+1ZewJASaQxz6gwCCNLM1SkfjuM/hPh1JAOh9jUUleJQF5MXx9RSho/VBQnorB3vbutaOQzw0yPLtDtSPKX8sVdhkveVQJAIDsJP5X8Tey6zXTUISor7PF0TSiKdE4k0IwKoy9y8HmQ+AU8+xyr/iOt5lvaGxKlBK8N/7yCw5H4qHnJaHT+Bg==";
		
		String lic = RSA.encryptByPrivate(code, pr);
		System.out.println(lic);
		
		String pb = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCLNbmKl9/gLn7Bef/xtUkshC1WyrLZLRpXCcFYR1gQi0isWsZBTicC4efBOkkNG3r+1ue0gvtuU/tjREFGf4Y7HaKHGb5tNCOlMNeNjM5YLRwLFqrUSsQyD4rj4eua1ltearr24R0HilnTvnQm6Z/UY0s21vdOUFQBPY0GNAa+0wIDAQAB";
		
		String s = RSA.decryptByPublic(lic, pb);
		System.out.println(s);
	}
	
}
