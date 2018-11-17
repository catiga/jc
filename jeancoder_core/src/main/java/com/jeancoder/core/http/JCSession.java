package com.jeancoder.core.http;

import javax.servlet.http.HttpSession;

public class JCSession {
	
	private HttpSession session;
	
	public JCSession(HttpSession session) {
		this.session = session;
	}
	
	public Object getAttribute(String name) {
		return session.getAttribute(name);
	}
	
	public void setAttribute(String name, Object value) {
		 session.setAttribute(name, value);
	}
	
	public String getId() {
		return session.getId();
	}
	
	public void setMaxInactiveInterval(int interval) {
		session.setMaxInactiveInterval(interval);
	}
	
}
