package com.jeancoder.root.io.http;

import static com.jeancoder.root.io.line.HeaderNames.HOST;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;

public class RequestFacade extends JCHttpRequest implements JCReqFaca {
	
	JCHttpRequest delegated;

	public RequestFacade(JCHttpRequest req) throws IOException {
		super(req.request);
		this.delegated = req;
	}

	public List<UploadFile> getUpfiles() {
		return delegated.getUpfiles();
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
	
	@Override
	public StringBuffer getRequestURL() {
		String domain = request.headers().get(HOST);
		String schema = request.headers().get(X_Forwarded_Proto);
		if(schema==null) {
			schema = "http";
		}
		return new StringBuffer(schema + "://" + domain + this.getRequestURI());
	}
	
	@Override
	public String getCharacterEncoding() {
		return delegated.getCharacterEncoding();
	}

	@Override
	public void setCharacterEncoding(String s) throws UnsupportedEncodingException {
		delegated.setCharacterEncoding(s);
	}

	@Override
	public int getContentLength() {
		return delegated.getIntHeader("Content-Length");
	}

	@Override
	public void setAttribute(String s, Object o) {
		delegated.setAttribute(s, o);
	}

	@Override
	public void removeAttribute(String s) {
		delegated.removeAttribute(s);
	}

	@Override
	public Locale getLocale() {
		return delegated.getLocale();
	}
	
	@Override
	public String getRemoteAddr() {
		return delegated.getRemoteAddr();
	}

	@Override
	public String getRemoteHost() {
		return delegated.getRemoteHost();
	}
	
	@Override
	public String getServerName() {
		return delegated.getServerName();
	}

	@Override
	public int getServerPort() {
		return delegated.getServerPort();
	}
	
}
