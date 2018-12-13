package com.jeancoder.core.util;

import com.jeancoder.core.http.JCThreadLocal;

public class MemCodeUtil {

	final static String _code_base_ = "0123456789ABCDEF";

	protected static String str2HexStr(String str) {
		char[] chars = _code_base_.toCharArray();
		StringBuilder sb = new StringBuilder("");
		byte[] bs = str.getBytes();
		int bit;
		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
		}
		return sb.toString().trim();
	}

	protected static String hexStr2Str(String hexStr) {
		String str = _code_base_;
		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[hexStr.length() / 2];
		int n;
		for (int i = 0; i < bytes.length; i++) {
			n = str.indexOf(hexs[2 * i]) * 16;
			n += str.indexOf(hexs[2 * i + 1]);
			bytes[i] = (byte) (n & 0xff);
		}
		return new String(bytes);
	}

	public static String getk(String k) {
		String app_code = JCThreadLocal.getCode();
		String new_k = "np://" + (app_code==null?"":app_code + "/") + MemCodeUtil.str2HexStr(k);
		return new_k;
	}
}
