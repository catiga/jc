package com.jeancoder.app.sdk.remote;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

import com.jeancoder.core.util.JackSonBeanMapper;

public class RemoteCall {
	
	public static void main(String[] argc) throws Exception {
		HCResp hp = http_call_stream("http://blog.sina.com.cn/s/blog_62b832910100vmbq.html", null, null);
		System.out.println(hp.getContent_length());
		System.out.println(hp.getContent_type());
	}
	
	private static ThreadLocal<Map<String, Object>> _ADMINER_ = new ThreadLocal<Map<String, Object>>();
	
	public static void http_header_set(Map<String, Object> properties) {
		_ADMINER_.set(properties);
	}
	
	public static Map<String, Object> http_header_get() {
		return _ADMINER_.get();
	}
	
	public static <T> T http_call(final Class<T> claz, String url, String param) {
		return http_call(claz, url, param, null);
	}
	
	public static String http_call(String url, String param) {
		return http_call(url, param, null);
	}
	
	public static <T> T http_call(final Class<T> claz, String url, String param, RequestCert cert) {
		String ret = http_call(url, param, cert);
		T obj = (T)JackSonBeanMapper.fromJson(ret, claz);
		return obj;
	}
	
	// DEFAULT POST METHOD
	public static String http_call(String url, String param, RequestCert cert) {
		String ret = HttpRequest.instance().getResponseString(url, param, HttpMethod.POST, cert);
		return ret;
	}
	
	public static HCResp http_call_stream(String url, String params, RequestCert cert) {
		InputStream inputstream = HttpRequest.instance().getResponseStringAsStream(url, params, HttpMethod.POST, cert);
		HCResp hp = null;
		try {
			byte[] content = readInputStream(inputstream);
			hp = new HCResp();
			hp.content = content;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return hp;
	}
	
	//SUPPORT METHOD SWITCH
	public static String http_call(String url, String param, RequestCert cert, HttpMethod method) {
		String ret = HttpRequest.instance().getResponseString(url, param, method, cert);
		return ret;
	}
	
	public static HCResp http_call_stream(String url, String params, RequestCert cert, HttpMethod method) {
		InputStream inputstream = HttpRequest.instance().getResponseStringAsStream(url, params, method, cert);
		HCResp hp = null;
		try {
			byte[] content = readInputStream(inputstream);
			hp = new HCResp();
			hp.content = content;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return hp;
	}
	
	
	private static byte[] readInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        //使用一个输入流从buffer里把数据读取出来
        while( (len=inStream.read(buffer)) != -1 ){
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        //关闭输入流
        inStream.close();
        //把outStream里的数据写入内存
        return outStream.toByteArray();
    }
}
