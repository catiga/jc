package com.jeancoder.root.server.util;

import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.jeancoder.root.server.config.CustomCmdConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeancoder.core.util.JackSonBeanMapper;
import com.jeancoder.root.server.mixture.JCLHealper;
import com.jeancoder.root.server.mixture.StringResults;
import com.jeancoder.root.server.state.RequestStateModel;

public class RemoteUtil {
	static Logger logger = LoggerFactory.getLogger(RemoteUtil.class);
	
	private static String singkey = null; 
	
	private static final String domain = "http://jcloudapp.chinaren.xyz";
//	private static final String domain = "http://e.local:8081";

	private static String convertJCSDomain(String jcsDomain) {
		if (jcsDomain == null) {
			return "";
		}
		if (jcsDomain.startsWith("jcs://")) {
			jcsDomain = jcsDomain.replace("jcs://", "http://");
		}
		if (jcsDomain.endsWith("/")) {
			jcsDomain = jcsDomain.substring(0, jcsDomain.length() - 1);
		}
		return jcsDomain;
	}

	public static String getConfigList(String confDomain) throws Exception {
		confDomain = convertJCSDomain(confDomain);
		Map<String,String> paramsMap = new TreeMap<String,String>();
		paramsMap.put("m_code", JCLHealper.INSTENSE.getMerchantsCode());
		paramsMap.put("m_instance", JCLHealper.INSTENSE.getInstanceNum());
		logger.info(confDomain + "/server/api/personal/common/getConfigList");
		logger.info("m_code:::{}", JCLHealper.INSTENSE.getMerchantsCode());
		logger.info("m_instance:::{}", JCLHealper.INSTENSE.getInstanceNum());
		String  rsaResultsJson = HttpsRequesUtil.connection(confDomain + "/server/api/personal/common/getConfigList", HttpsRequesUtil.getParams(paramsMap, getSignKey()));
		return rsaResultsJson;
	}
		
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
		logger.info(domain + "/server/api/personal/common/getConfigList");
		logger.info("m_code:::" + JCLHealper.INSTENSE.getMerchantsCode());
		logger.info("m_instance:::" + JCLHealper.INSTENSE.getInstanceNum());

		String customFetchDomain = CustomCmdConf.getInstance().getFetchDomain();
		customFetchDomain = customFetchDomain == null ? domain : customFetchDomain;

		String  rsaResultsJson = HttpsRequesUtil.connection(customFetchDomain + "/server/api/personal/common/getConfigList", HttpsRequesUtil.getParams(paramsMap, getSignKey()));
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
	
	public static String uploadPerfData(List<RequestStateModel> data) throws Exception {
		Map<String,String> paramsMap = new TreeMap<String,String>();
		paramsMap.put("m_code", JCLHealper.INSTENSE.getMerchantsCode());
		paramsMap.put("m_instance", JCLHealper.INSTENSE.getInstanceNum());
		paramsMap.put("pfdata", JackSonBeanMapper.listToJson(data));
		String  rsaResultsJson = HttpsRequesUtil.connection(domain + "/server/api/personal/common/perf_data", HttpsRequesUtil.getParams(paramsMap, getSignKey()));
		return rsaResultsJson;
	}
	
	
	public static String getSignKey() throws Exception {
		if (singkey != null) {
			return singkey;
		}
		Map<String,String> params = new TreeMap<String,String>();
		String m_code = JCLHealper.INSTENSE.getMerchantsCode();
		String ins_code = JCLHealper.INSTENSE.getInstanceNum();
		params.put("m_code", m_code);
		params.put("m_instance", ins_code);
		String parameter = HttpsRequesUtil.getParameter(params);
		logger.info("check sign key:::" + domain + "/server/api/sys/getSignKey");
		String enc_param = encryptByPublic(parameter);
		logger.info("parameter=" + enc_param);
		String rsaResultsJson = HttpsRequesUtil.connection(domain + "/server/api/sys/getSignKey", "parameter=" + enc_param + "&m_code=" + m_code + "&ins_code=" + ins_code);
		logger.info("check sign key result:::" + rsaResultsJson);;
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
