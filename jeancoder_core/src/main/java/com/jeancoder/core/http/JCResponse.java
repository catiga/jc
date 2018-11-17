package com.jeancoder.core.http;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;

import javax.servlet.http.HttpServletResponse;

public class JCResponse {

	private HttpServletResponse response;
	
	public JCResponse(HttpServletResponse response){
		this.response = response;
	}

	public ServletOutputStream getOutputStream() throws IOException {
		return response.getOutputStream();
	}

	public PrintWriter getWriter() throws IOException {
		return response.getWriter();
	}

	public void setCharacterEncoding(String charset) {
		response.setCharacterEncoding(charset);		
	}


	public void setContentType(String type) {
		response.setContentType(type);		
	}

	public void addCookie(JCCookie cookie) {
		response.addCookie(cookie.getCookie());
	}


	public void setDateHeader(String name, long date) {
		response.setDateHeader(name, date);		
	}


	public void setHeader(String name, String value) {
		response.setHeader(name, value);		
	}

	public void setStatus(int sc) {
		response.setStatus(sc);		
	}
	
	public void sendRedirect(String location) throws IOException {
		response.sendRedirect(location);		
	}
	
	
	public void setContentLength(Integer len) {
		response.setContentLength(len);
	}

}
