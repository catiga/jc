package com.jeancoder.root.io.http;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.Cookie;

public class RequestFacade extends JCHttpRequest implements JCReqFaca {
	
	JCHttpRequest delegated;

	public RequestFacade(JCHttpRequest req) throws IOException {
		super(req.request);
		this.delegated = req;
	}

	public String getRequestURI() {
		Enumeration<String> attributes = delegated.getAttributeNames();
		while(attributes.hasMoreElements()) {
			String attrName = (String) attributes.nextElement();
			if(attrName.equals(JCReqFaca.FORWARD_REQUEST_FULLURI)) {
				return delegated.getAttribute(attrName).toString();
			}
		}
		return delegated.getRequestURI();
	}
	
	public String getContextPath() {
		Enumeration<String> attributes = delegated.getAttributeNames();
		while(attributes.hasMoreElements()) {
			String attrName = (String) attributes.nextElement();
			if(attrName.equals(JCReqFaca.FORWARD_REQUEST_CONTEXT)) {
				return delegated.getAttribute(attrName).toString();
			}
		}
		return delegated.getContextPath();
	}
	
	public String getServletPath() {
		Enumeration<String> attributes = delegated.getAttributeNames();
		while(attributes.hasMoreElements()) {
			String attrName = (String) attributes.nextElement();
			if(attrName.equals(JCReqFaca.FORWARD_REQUEST_PATH)) {
				return delegated.getAttribute(attrName).toString();
			}
		}
		return delegated.getServletPath();
	}
	
	public String getPathInfo() {
		Enumeration<String> attributes = delegated.getAttributeNames();
		while(attributes.hasMoreElements()) {
			String attrName = (String) attributes.nextElement();
			if(attrName.equals(JCReqFaca.FORWARD_REQUEST_PATH)) {
				return delegated.getAttribute(attrName).toString();
			}
		}
		return delegated.getPathInfo();
	}
	
	public String getQueryString() {
		Enumeration<String> attributes = delegated.getAttributeNames();
		while(attributes.hasMoreElements()) {
			String attrName = (String) attributes.nextElement();
			if(attrName.equals(JCReqFaca.FORWARD_REQUEST_QUERY)) {
				return delegated.getAttribute(attrName).toString();
			}
		}
		return delegated.getQueryString();
	}
	
	@Override
	public Cookie[] getCookies() {
		return delegated.getCookies();
	}
	
	public String getMethod() {
		return delegated.getMethod();
	}
	
	@Override
	public Object getAttribute(String s) {
		return delegated.getAttribute(s);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return delegated.getAttributeNames();
	}
	
	@Override
	public String getContentType() {
		return delegated.getContentType();
	}
	
	@Override
	public String getParameter(String s) {
		return delegated.getParameter(s);
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return delegated.getParameterNames();
	}

	@Override
	public String[] getParameterValues(String s) {
		return delegated.getParameterValues(s);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return delegated.getParameterMap();
	}
	
}
