package com.jeancoder.core.http;

import javax.servlet.http.Cookie;

public class JCCookie {
	
	public Cookie cookie;
	
	public JCCookie(String name, String value) {
		cookie = new Cookie(name, value);
	}
	
	public JCCookie(Cookie cookie){
		this.cookie =  cookie;
	}
	
	public String getName(){
		return cookie.getName();
	}
	
	public String getValue(){
		return cookie.getValue();
	}
	
	public Cookie getCookie(){
		return cookie;
	}

	public void setPath(String uri) {
        cookie.setPath(uri);
    }

    public String getPath() {
        return cookie.getPath();
    }
    public void setMaxAge(Integer expiry) {
    	cookie.setMaxAge(expiry);
    }
    
    
}
