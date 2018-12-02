package com.jeancoder.root.io.http;

import static com.jeancoder.root.io.line.HeaderNames.CONTENT_ENCODING;
import static com.jeancoder.root.io.line.HeaderNames.LOCATION;
import static com.jeancoder.root.io.line.HeaderNames.SET_COOKIE;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.jeancoder.root.io.JCWriter;
import com.jeancoder.root.io.JcServletOutputStream;

import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;

public class JCHttpResponse implements HttpServletResponse {

    private String contentType;

    private int contentLength = -1;

    private int status;

    private String encoding = "UTF-8";

    private JCWriter writer;
    
    JcServletOutputStream output = new JcServletOutputStream();

    private Map<String, String> headers = new HashMap<String, String>();
    
    private FullHttpResponse response;

    public JCHttpResponse() {
    	response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
    	response.headers().add(CONTENT_ENCODING, encoding);
    }

    public JCHttpResponse(FullHttpResponse response) {
    	response.headers().add(CONTENT_ENCODING, encoding);
    	this.response = response;
    }
    
    public FullHttpResponse delegateObj() {
		return response;
	}
    
    public void replaceDelegateObj(FullHttpResponse new_res) {
    	this.response = new_res;
    }

	public Map<String, String> getHeaders() {
        return headers;
    }

    public int getContentLength() {
        return contentLength;
    }

    @Override
    public void setContentLength(int i) {
        contentLength = i;
        setIntHeader("Content-Length", i);
    }

	public String getResult() {
        if (writer == null) {
            return "";
        }
        return writer.toString();
    }

    @Override
    public void addCookie(Cookie cookie) {
    	io.netty.handler.codec.http.cookie.Cookie net_coo = new DefaultCookie(cookie.getName(), cookie.getValue());
    	if(cookie.getDomain()!=null) {
    		net_coo.setDomain(cookie.getDomain());
    	}
    	if(cookie.getMaxAge()>0) {
    		net_coo.setMaxAge(Long.valueOf(cookie.getMaxAge()));
    	} else if(cookie.getMaxAge()==0) {
    		net_coo.setMaxAge(0x0L);
    	} else {
    		net_coo.setMaxAge(io.netty.handler.codec.http.cookie.Cookie.UNDEFINED_MAX_AGE);
    	}
    	if(cookie.getPath()!=null) {
    		net_coo.setPath(cookie.getPath());
    	} else {
    		net_coo.setPath("/");
    	}
    	
    	String real_cookie = ServerCookieEncoder.STRICT.encode(net_coo);
    	response.headers().add(SET_COOKIE, real_cookie);
    	System.out.println(response);
    }

    @Override
    public boolean containsHeader(String s) {
        return headers.get(s) != null;
    }

    @Override
    public String encodeURL(String s) {
        return s;
    }

    @Override
    public String encodeRedirectURL(String s) {
        throw new UnsupportedOperationException();
    }

    /**
     * @param s
     * @deprecated
     */
    @Override
    public String encodeUrl(String s) {
        return s;
    }

    /**
     * @param s
     * @deprecated
     */
    @Override
    public String encodeRedirectUrl(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendError(int i, String s) throws IOException {
        sendError(i, s);
    }

    @Override
    public void sendError(int i) throws IOException {
        sendError(i, null);
    }

    @Override
    public void sendRedirect(String s) throws IOException {
        setStatus(302);
        setHeader(LOCATION, s);
        response.setStatus(HttpResponseStatus.FOUND);
        response.headers().add(LOCATION, s);
        
    }

    @Override
    public void setDateHeader(String s, long l) {
        //throw new UnsupportedOperationException();
    }

    @Override
    public void addDateHeader(String s, long l) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setHeader(String s, String s1) {
        headers.put(s, s1);
    }

    @Override
    public void addHeader(String s, String s1) {
        headers.put(s, s1);
    }

    @Override
    public void setIntHeader(String s, int i) {
        setHeader(s, "" + i);
    }

    @Override
    public void addIntHeader(String s, int i) {
        setHeader(s, "" + i);
    }

    /**
     * @param i
     * @param s
     * @deprecated
     */
    @Override
    public void setStatus(int i, String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public void setStatus(int i) {
        status = i;
    }

    @Override
    public String getHeader(String s) {
        return headers.get(s);
    }

    @Override
    public Collection<String> getHeaders(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<String> getHeaderNames() {
        return headers.keySet();
    }

    @Override
    public String getCharacterEncoding() {
        return encoding;
    }

    @Override
    public void setCharacterEncoding(String s) {
        encoding = s;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public void setContentType(String s) {
        setHeader("Content-Type", s);
        contentType = s;
        String charset = "charset=";
        int i = s.indexOf(charset);
        if (i >= 0) {
            encoding = s.substring(i + charset.length());
        }
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
    	return output;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        writer = new JCWriter(output, false);
        return new PrintWriter(writer);
    }

    @Override
    public int getBufferSize() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBufferSize(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void flushBuffer() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void resetBuffer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCommitted() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Locale getLocale() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLocale(Locale locale) {
        throw new UnsupportedOperationException();
    }
}

