package com.jeancoder.core.power.localdns;

import java.net.URL;

public class UrlAddress {

	String original_url;
	
	String protocol;
	String host;
	Integer port;
	String uri;
	String param;
	
	public String getHost() {
		return host;
	}
	
	public String getParam() {
		return param;
	}
	
	public UrlAddress(String url) {
		this.original_url = url;
		this.init();
	}
	
	public static void main(String[] argc) throws Exception {
		//String regex = "^(?:https?://)?[\\w]{1,}(?:\\.?[\\w]{1,})+[\\w-_/?&=#%:]*$";
		//String regex = "^([hH][tT]{2}[pP]:/*|[hH][tT]{2}[pP][sS]:/*|[fF][tT][pP]:/*)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+(\\?{0,1}(([A-Za-z0-9-~]+\\={0,1})([A-Za-z0-9-~]*)\\&{0,1})*)$";
		String url = "httpS://192.168.1.1:80?oc=1000&on=101180808191832150001&sign=f0b93251487742f6b61623e334301891";
		
		UrlAddress ua = new UrlAddress(url);
		System.out.println(ua.protocol);
		System.out.println(ua.host);
		System.out.println(ua.port);
		System.out.println(ua.param);
		System.out.println(ua.uri);
		
		System.out.println(ua.isHttps());
		System.out.println(ua.requestPath("127.0.0.1"));
		System.out.println(ua);
	}
	
	private void init() {
		try {
			URL url = new URL(this.original_url);
			this.protocol = url.getProtocol();
			this.param = url.getQuery();
			this.port = url.getPort()>0?url.getPort():80;
			this.host = url.getHost();
			this.uri = url.getPath();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public String requestDomain() {
		return protocol + "://" + host + (port==80?"":":" + port);
	}
	
	public String requestPath() {
		return protocol + "://" + host + (port==80?"":":" + port) + uri;
	}
	
	public String requestPath(String new_host) {
		return protocol + "://" + new_host + (port==80?"":":" + port) + uri;
	}
	
	public String requestPath(Integer deploy) {
		String new_host = deploy.equals(1)?"127.0.0.1":host;
		return protocol + "://" + new_host + (port==80?"":":" + port) + uri;
	}
	
	public String getProtocol() {
		return protocol;
	}

	public Integer getPort() {
		return port;
	}

	public String getUri() {
		return uri;
	}

	@Override
	public String toString() {
		return protocol + "://" + host + (port==80?"":":" + port) + uri + "?" + param;
	}
	
	public void changeProto(String new_proto) {
		this.protocol = new_proto;
	}
	
	/**
	 * 
	 * @param deploy
	 * 0:代表远程，默认值，不替换host
	 * 1:代表本机，替换host
	 * @return
	 */
	public String toString(Integer deploy) {
		String new_host = deploy.equals(1)?"127.0.0.1":host;
		return protocol + "://" + new_host + (port==80?"":":" + port) + uri + "?" + param;
	}

	public boolean isHttps() {
		return protocol.toUpperCase().equals("HTTPS")?true:false;
	}
}
