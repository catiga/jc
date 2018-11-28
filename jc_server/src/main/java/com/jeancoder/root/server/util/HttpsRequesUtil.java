package com.jeancoder.root.server.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import com.jeancoder.core.util.MD5Util;

public class HttpsRequesUtil {
	
	public static String key = "";
	
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
			// 读入参数
			httpsConn.setDoInput( true);
			httpsConn.setDoOutput(true);
			 // 获取URLConnection对象对应的输出流
            OutputStream os = httpsConn.getOutputStream();
            os.write(parameter.getBytes());//post的参数 xx=xx&yy=yy
            // flush输出流的缓冲
            os.flush();
			int responseCode = httpsConn.getResponseCode();
			//print responseCode
			BufferedReader br = new BufferedReader(new InputStreamReader(httpsConn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
		    while ((inputLine = br.readLine()) != null) {
		        response.append(inputLine);
		    }
			return response.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static InputStream connectionStream(String urlStr, String parameter){
		try{
			URL reqURL = new URL(urlStr); //创建URL对象
			HttpURLConnection httpsConn = (HttpURLConnection)reqURL.openConnection();
			//建立连接的超时时间
			httpsConn.setConnectTimeout(2000000);
			//传递连接的超时时间 
			httpsConn.setReadTimeout(3000000);
			httpsConn.setRequestMethod("POST");
			httpsConn.setRequestProperty("Connection", "Keep-Alive");
			httpsConn.setRequestProperty("Charset", "UTF-8");
			// 读入参数
			httpsConn.setDoInput( true);
			httpsConn.setDoOutput(true);
			// 获取URLConnection对象对应的输出流
            OutputStream os = httpsConn.getOutputStream();
            os.write(parameter.getBytes()); 
            os.flush();
			int responseCode = httpsConn.getResponseCode();
            InputStream is = httpsConn.getInputStream();
            return is;
		} catch (Exception e) {
			e.printStackTrace();
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
