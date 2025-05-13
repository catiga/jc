package com.jeancoder.root.server.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import com.jeancoder.core.util.MD5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpsRequesUtil {

	private static Logger logger = LoggerFactory.getLogger(HttpsRequesUtil.class);
	
	public static String key = "";
	
	@SuppressWarnings("unused")
	public static String connection(String urlStr, String parameter){
		try{
			URL reqURL = new URL(urlStr); //创建URL对象
			HttpURLConnection httpsConn = (HttpURLConnection)reqURL.openConnection();
			//建立连接的超时时间
			httpsConn.setConnectTimeout(200000);
			//传递连接的超时时间 
			httpsConn.setReadTimeout(300000);
			httpsConn.setRequestMethod("POST");
			httpsConn.setRequestProperty("Connection", "Keep-Alive");
			httpsConn.setRequestProperty("Charset", "UTF-8");
			httpsConn.setDoInput( true);
			httpsConn.setDoOutput(true);
            OutputStream os = httpsConn.getOutputStream();
            os.write(parameter.getBytes());//post的参数 xx=xx&yy=yy
            os.flush();
			int responseCode = httpsConn.getResponseCode();
			BufferedReader br = new BufferedReader(new InputStreamReader(httpsConn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
		    while ((inputLine = br.readLine()) != null) {
		        response.append(inputLine);
		    }
			return response.toString();
		} catch (Exception e) {
			logger.error("connect server {} with param {}, but got error.", urlStr, parameter, e);
		}
		return "";
	}
	
	@SuppressWarnings("unused")
	public static InputStream connectionStream(String urlStr, String parameter){
		try{
			URL reqURL = new URL(urlStr);
			HttpURLConnection httpsConn = (HttpURLConnection)reqURL.openConnection();
			httpsConn.setConnectTimeout(2000000);
			httpsConn.setReadTimeout(3000000);
			httpsConn.setRequestMethod("POST");
			httpsConn.setRequestProperty("Connection", "Keep-Alive");
			httpsConn.setRequestProperty("Charset", "UTF-8");
			httpsConn.setDoInput( true);
			httpsConn.setDoOutput(true);
            OutputStream os = httpsConn.getOutputStream();
            os.write(parameter.getBytes()); 
            os.flush();
			int responseCode = httpsConn.getResponseCode();
            return httpsConn.getInputStream();
		} catch (Exception e) {
			logger.error("connect server {} with param {}, but got error.", urlStr, parameter, e);
		}
		return null;
	}

	public static InputStream connectionStreamEnhance(String urlStr, String parameter){
		try {
			URL reqURL = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) reqURL.openConnection();
			conn.setConnectTimeout(5000);      // 5~10 Seconds
			conn.setReadTimeout(15000);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setDoInput(true);
			conn.setDoOutput(true);

			try (OutputStream os = conn.getOutputStream()) {
				os.write(parameter.getBytes("UTF-8"));
				os.flush();
			}

			int responseCode = conn.getResponseCode();
			String contentType = conn.getContentType();

			if (responseCode != 200) {
				logger.error("Central Server HTTP error: {}, content-type: {}", responseCode, contentType);
				InputStream errStream = conn.getErrorStream();
				if (errStream != null) {
					// 可选：读取内容作为日志
					String body = new BufferedReader(new InputStreamReader(errStream))
							.lines().collect(Collectors.joining("\n"));
					logger.error("Central Server error response: \n{}", body);
				}
				return null;
			}

			logger.info("Central Server  content-type: {}", contentType);

			return conn.getInputStream();

		} catch (Exception e) {
			logger.error("Connect server {} with param {}, but got error.", urlStr, parameter, e);
		}
		return null;
	}
	
	public  static String getParameter(Map<String,String> params) {
		StringBuffer sb = new StringBuffer();
		for (Iterator<String> iter = params.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String values = params.get(name);
			if (sb.length() != 0) {
				sb.append("&");
			}
			sb.append(name + "=" + values);
		}
		return sb.toString();
	}
	
	public  static String getParams(Map<String,String> map, String sigkey) {
		String parameter = HttpsRequesUtil.getParameter(map);
		String signMd5 = MD5Util.getStringMD5(parameter + sigkey);
		map.put("sign", signMd5);
		return HttpsRequesUtil.getParameter(map);
	}
	
}
