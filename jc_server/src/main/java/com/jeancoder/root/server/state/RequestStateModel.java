package com.jeancoder.root.server.state;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.Calendar;

import com.jeancoder.core.util.JackSonBeanMapper;

import io.netty.handler.codec.http.HttpRequest;

@SuppressWarnings("serial")
public class RequestStateModel implements Serializable {
	
	String uri;
	
	Long reqTime;
	
	Long resTime;
	
	Integer statusCode;
	
	String remoteAddr;
	
	String userAgent;
	
	String errInfo;
	
	public RequestStateModel(HttpRequest request) {
		this.uri = request.uri();
		this.reqTime = Calendar.getInstance().getTimeInMillis();
		this.userAgent = JackSonBeanMapper.toJson(request.headers());
		
	}

	public String getUri() {
		return uri;
	}

	public Long getReqTime() {
		return reqTime;
	}

	public String getRemoteAddr() {
		return remoteAddr;
	}

	public void init(HttpRequest request, InetSocketAddress remote) {
		this.remoteAddr = remote.getAddress().getHostAddress();
		String clientIP = request.headers().get("X-Forwarded-For");
		if(clientIP!=null) {
			this.remoteAddr = clientIP;
		}
		this.userAgent = request.headers().get("User-Agent");
	}
	
	public String getUserAgent() {
		return userAgent;
	}

	public Long getResTime() {
		return resTime;
	}

	public void setResTime(Long resTime) {
		this.resTime = resTime;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getErrInfo() {
		return errInfo;
	}

	public void setErrInfo(String errInfo) {
		this.errInfo = errInfo;
	}

	public int length() {
		int len = 0;
		if(uri!=null) {
			len += uri.getBytes().length;
		}
		if(reqTime!=null) {
			len += Long.SIZE>>>3;
		}
		if(resTime!=null) {
			len += Long.SIZE>>>3;
		}
		if(statusCode!=null) {
			len += Integer.SIZE>>>3;
		}
		if(remoteAddr!=null) {
			len += remoteAddr.getBytes().length;
		}
		if(userAgent!=null) {
			len += userAgent.getBytes().length;
		}
		if(errInfo!=null) {
			len += errInfo.getBytes().length;
		}
		return len;
	}
	
}
