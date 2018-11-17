package com.jeancoder.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

public class MD5Util {

	/**
	 * <i>加密</i>
	 * 
	 * @version 1.0.0
	 */
	public final static String getStringMD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] strTemp = s.getBytes();
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}
	
	public final static String getFileMD5(String path) {
		return getFileMD5(new File(path));
	}
	
	public final static String getFileMD5(File file) {
		BigInteger bi = null;
		try {
			byte[] buffer = new byte[8192];
			int len = 0;
			MessageDigest md = MessageDigest.getInstance("MD5");
			FileInputStream fis = new FileInputStream(file);
			
			while ((len = fis.read(buffer)) != -1) {
				md.update(buffer, 0, len);
			}
			fis.close();
			byte[] b = md.digest();
			bi = new BigInteger(1, b);
			return bi.toString(16);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] argc) {
		String f = "/Users/jackielee/Desktop/1.rtf";
		System.out.println(getStringMD5(f));
		System.out.println(getFileMD5(f));
	}
}
