package com.jeancoder.root.server.util;

import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

import com.jeancoder.core.util.JackSonBeanMapper;
import com.jeancoder.root.server.mixture.JCLHealper;
import com.jeancoder.root.server.mixture.StringResults;

public class RemoteUtil {
	
	private static String singkey = null; 
		
	/**
	 * 获取配置信息
	 * @param signkey
	 * @return
	 * @throws Exception 
	 */
	public static String getConfigList() throws Exception {
		Map<String,String> paramsMap = new TreeMap<String,String>();
		paramsMap.put("m_code", JCLHealper.INSTENSE.getMerchantsCode());
		paramsMap.put("m_instance", JCLHealper.INSTENSE.getInstanceNum());
		String  rsaResultsJson = HttpsRequesUtil.connection("http://192.168.1.9:8080/server/api/personal/common/getConfigList", HttpsRequesUtil.getParams(paramsMap, getSignKey()));
		return rsaResultsJson;
	}
	
	/**
	 * 下载zip包
	 * @param signkey
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public static InputStream  installation(String fetch_address, Long id) throws Exception {
		Map<String,String> paramsMap = new TreeMap<String,String>();
		paramsMap.put("id", id.toString());
		paramsMap.put("m_code", JCLHealper.INSTENSE.getMerchantsCode());
		paramsMap.put("m_instance", JCLHealper.INSTENSE.getInstanceNum());
		InputStream  zis = HttpsRequesUtil.connectionStream(fetch_address, HttpsRequesUtil.getParams(paramsMap,  getSignKey()));
		return zis;
	}
	
	
	public static String getSignKey() throws Exception {
		if (singkey != null) {
			return singkey;
		}
		Map<String,String> params = new TreeMap<String,String>();
		params.put("m_code", JCLHealper.INSTENSE.getMerchantsCode());
		String parameter = HttpsRequesUtil.getParameter(params);
		String rsaResultsJson = HttpsRequesUtil.connection("http://192.168.1.9:8080/server/api/sys/getSignKey", "parameter=" + encryptByPublic(parameter));
		rsaResultsJson = decryptByPublic(rsaResultsJson);
		StringResults strResult = JackSonBeanMapper.fromJson(rsaResultsJson, StringResults.class);
		
		if (!"0000".equals(strResult.getStatus())) {
			throw new Exception(strResult.getMsg());
		}
		singkey = strResult.getResults();
		return  singkey;
		
	}
	
	/**
	 * 公钥加密
	 * @param parameter
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private static String encryptByPublic(String parameter) {
		String parameters = parameter;
		parameters = JCLHealper.INSTENSE.encryptByPublic(parameters);
		parameters = parameters.replaceAll("\r|\n", "");
		return URLEncoder.encode(parameters); // 替换特殊字符
	}
	
	/**
	 * 公钥解密
	 * @param parameter
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private static String decryptByPublic(String parameter) {
		String str  = URLDecoder.decode(parameter);// 替换特殊字符
		str = JCLHealper.INSTENSE.decryptByPublic(str);
		return str; 
	}
	
}
