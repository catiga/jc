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
	
	String scheme;
	
	String remoteAddr;
	
	String userAgent;
	
	String otherInfo;
	
	String errInfo;
	
	String contentType;
	
	public RequestStateModel(HttpRequest request) {
		this.uri = request.uri();
		this.reqTime = Calendar.getInstance().getTimeInMillis();
		this.userAgent = JackSonBeanMapper.toJson(request.headers());
		
	}

	public String getScheme() {
		return scheme;
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

	public String getOtherInfo() {
		return otherInfo;
	}

	public void setOtherInfo(String otherInfo) {
		this.otherInfo = otherInfo;
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

	public String getContentType() {
		return contentType;
	}

	public int length() {
		int len = 0;
		if(uri!=null) {
			len += uri.length();
		}
		if(reqTime!=null) {
			len += reqTime.toString().length();
		}
		if(resTime!=null) {
			len += resTime.toString().length();
		}
		if(statusCode!=null) {
			len += statusCode.toString().length();
		}
		if(scheme!=null) {
			len += scheme.length();
		}
		if(remoteAddr!=null) {
			len += remoteAddr.length();
		}
		if(userAgent!=null) {
			len += userAgent.length();
		}
		if(otherInfo!=null) {
			len += otherInfo.length();
		}
		if(errInfo!=null) {
			len += errInfo.length();
		}
		if(contentType!=null) {
			len += contentType.length();
		}
		return len;
	}
}
