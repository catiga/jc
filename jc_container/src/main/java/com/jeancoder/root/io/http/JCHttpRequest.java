package com.jeancoder.root.io.http;

import static com.jeancoder.root.io.line.HeaderNames.COOKIE;
import static com.jeancoder.root.io.line.HeaderNames.HOST;
import static com.jeancoder.root.io.line.JCIO.JCHOST;
import static com.jeancoder.root.io.line.JCIO.JCPORT;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.ReadListener;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;

public class JCHttpRequest implements HttpServletRequest {
	
    private io.netty.handler.codec.http.HttpRequest request;

    private String uri;

	protected Hashtable<String, Object> attributes = new Hashtable<String, Object>();

    private String characterEncoding;

    private String contentType;

    //path parameter
    private Map<String, String[]> parameters;

    private boolean inputStreamUsed = false;

    private boolean readerUsed = false;

    private Cookie[] cookies;
    
    //The content of the HTTP POST.
    private String postData;
    
    private InetSocketAddress remoteHost;
    
    static final RequestParser reqpar = new RequestParser();

    public JCHttpRequest(FullHttpRequest request) throws IOException {
        this.request = request;
        uri = request.uri();
        HttpHeaders headers = request.headers();
        
        String value = headers.get(COOKIE);
		if (value!=null) {
			Set<io.netty.handler.codec.http.cookie.Cookie> cookie_sets = ServerCookieDecoder.LAX.decode(value);
			if(cookie_sets!=null&&!cookie_sets.isEmpty()) {
				cookies = new Cookie[cookie_sets.size()];
				Iterator<io.netty.handler.codec.http.cookie.Cookie> co_set_its = cookie_sets.iterator();
				int i = 0;
				while(co_set_its.hasNext()) {
					io.netty.handler.codec.http.cookie.Cookie net_cookie = co_set_its.next();
					Cookie real_cookie = new Cookie(net_cookie.name(), net_cookie.value());
					real_cookie.setDomain(net_cookie.domain()==null?"/":net_cookie.domain());
					real_cookie.setHttpOnly(net_cookie.isHttpOnly());
					real_cookie.setMaxAge(net_cookie.maxAge()>Integer.MAX_VALUE? Integer.MAX_VALUE:(int)net_cookie.maxAge());
					real_cookie.setPath(net_cookie.path());
					real_cookie.setSecure(net_cookie.isSecure());
					try {
						real_cookie.setVersion(Integer.valueOf(net_cookie.value()));
					}catch(NumberFormatException nfex) {}
					cookies[i++] = real_cookie;
				}
			}
		}
		parameters = reqpar.parse((FullHttpRequest)request);
       
        contentType = headers.get("CONTENT-TYPE");
        if (contentType == null) {
            contentType = "application/x-www-form-urlencoded";
        }
        postData = request.content().toString();
    }

    public void setRemoteHost(InetSocketAddress remoteHost) {
		this.remoteHost = remoteHost;
	}

	@Override
    public String getAuthType() {
        return "";
    }

    @Override
    public Cookie[] getCookies() {
        return cookies;
    }

    @Override
    public long getDateHeader(String s) {
        return System.currentTimeMillis();
    }

    @Override
    public String getHeader(String s) {
        return request.headers().get(s);
    }

    @Override
    public Enumeration<String> getHeaders(String s) {
        throw new UnsupportedOperationException("getHeaders(String s)");
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        throw new UnsupportedOperationException("getHeaderNames");
    }

    @Override
    public int getIntHeader(String s) {
        String value = getHeader(s);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException exception) {
                return -1;
            }
        } else {
            return -1;
        }
    }

    @Override
    public String getMethod() {
        return request.method().name();
    }

    @Override
    public String getPathInfo() {
        return request.uri();
    }

    @Override
    public String getPathTranslated() {
        return null;
    }

    @Override
    public String getContextPath() {
    	String uri = request.uri();
    	if(uri.length()>1) {
    		StringBuffer buff = new StringBuffer();
    		int start = 0;
    		for(;;) {
    			if(start>=uri.length()) {
    				break;
    			}
    			char c;
    			if((c = uri.charAt(start++))!='/'&&c!='?'&&c!='#') {
    				buff.append(c + "");
    			} else {
    				if(buff.length()>0) {
    					break;
    				}
    			}
    		}
    		buff.insert(0, '/');
    		return buff.toString();
    	} else {
    		return "/";
    	}
    }

    @Override
    public String getQueryString() {
    	if (uri.indexOf('?') == -1) {
            return null;
        } else {
            return uri.substring(uri.indexOf('?') + 1);
        }
    }

    @Override
    public String getRemoteUser() {
        return "";
    }

    @Override
    public boolean isUserInRole(String s) {
        return false;
    }

    @Override
    public Principal getUserPrincipal() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRequestedSessionId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRequestURI() {
        if (uri.indexOf('?') == -1) {
            return uri;
        } else {
            return uri.substring(0, uri.indexOf('?'));
        }
    }

    @Override
    public StringBuffer getRequestURL() {
        return new StringBuffer(uri);
    }

    @Override
    public String getServletPath() {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpSession getSession(boolean b) {
        return null;
    }

    @Override
    public HttpSession getSession() {
        return null;
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return true;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    /**
     * @deprecated
     */
    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    @Override
    public boolean authenticate(HttpServletResponse httpServletResponse) throws IOException, ServletException {
        return false;
    }

    @Override
    public void login(String s, String s1) throws ServletException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void logout() throws ServletException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return null;
    }

    @Override
    public Part getPart(String s) throws IOException, ServletException {
        return null;
    }

    @Override
    public Object getAttribute(String s) {
        return attributes.get(s);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return attributes.keys();
    }

    @Override
    public String getCharacterEncoding() {
        if (characterEncoding != null) {
            return characterEncoding;
        } else if (contentType == null) {
            return null;
        } else {
            int charsetPos = contentType.indexOf("charset=");
            if (charsetPos == -1) {
                return "UTF-8";
            } else {
                return contentType.substring(charsetPos + 8);
            }
        }
    }

    @Override
    public void setCharacterEncoding(String s) throws UnsupportedEncodingException {
        this.characterEncoding = s;
    }

    @Override
    public int getContentLength() {
        return getIntHeader("Content-Length");
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (readerUsed) {
            throw new IllegalStateException("The method getReader() has already been called on this request.");
        }
        inputStreamUsed = true;
        return new InputStream(postData);
    }

    @Override
    public String getParameter(String s) {
        String[] values = getParameterValues(s);
        return (values == null) ? null : values[0];
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return null;
    }

    @Override
    public String[] getParameterValues(String s) {
        Object values = parameters.get(s);
        if (values == null) {
            return null;
        } else if (values instanceof String) {
            return new String[]{(String) values};
        } else {
            //ArrayList<String> list = (ArrayList<String>) values;
            //return (String[]) list.toArray(new String[list.size()]);
        	
        	return parameters.get(s);
        }
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return parameters;
    }

    @Override
    public String getProtocol() {
        return "file://";
    }

    @Override
    public String getScheme() {
        int separator = uri.indexOf("://");
        if (separator != -1) {
            return uri.substring(0, separator + 3);
        }
        return "file://";
    }

    @Override
    public String getServerName() {
        try {
			return InetAddress.getLocalHost().getHostName();
        } catch (Exception ioe) {
            return "localhost";
        }
    }

    @Override
    public int getServerPort() {
    	
        return 0;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (inputStreamUsed) {
            throw new IllegalStateException("The method getInputStream() has already been called on this request.");
        }
        readerUsed = true;
        return new BufferedReader(new StringReader(postData));
    }

    @Override
    public String getRemoteAddr() {
    	String clientIP = request.headers().get("X-Forwarded-For");
    	if(clientIP==null) {
    		return remoteHost.getAddress().getHostAddress();
    	}
        return clientIP;
    }

    @Override
    public String getRemoteHost() {
    	if(remoteHost!=null) {
    		return remoteHost.getHostName();
    	}
        return "localhost";
    }

    @Override
    public void setAttribute(String s, Object o) {
        attributes.put(s, o);
    }

    @Override
    public void removeAttribute(String s) {
        attributes.remove(s);
    }

    @Override
    public Locale getLocale() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Enumeration<Locale> getLocales() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String s) {
        throw new UnsupportedOperationException();
    }

    /**
     * @param s
     * @deprecated
     */
    @Override
    public String getRealPath(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getRemotePort() {
        return -1;
    }

    @Override
    public String getLocalName() {
        return "Servlet Server";
    }

    @Override
    public String getLocalAddr() {
    	String server_host = request.headers().get(HOST);
    	String host_name = JCHOST;
    	if(server_host!=null) {
    		host_name = server_host;
    		if(host_name.indexOf(":")>-1) {
    			host_name = host_name.substring(0, host_name.indexOf(":"));
    		}
    	}
        return host_name;
    }

    @Override
    public int getLocalPort() {
    	String server_host = request.headers().get(HOST);
    	int host_port = JCPORT;
    	if(server_host!=null) {
    		if(server_host.indexOf(":")>-1) {
    			try {
    				host_port = Integer.valueOf(server_host.substring(server_host.indexOf(":") + 1));
    			}catch(NumberFormatException nex) {}
    		} else {
    			host_port = 80;
    		}
    	}
        return host_port;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return null;
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return null;
    }

    @Override
    public boolean isAsyncStarted() {
        return false;
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return null;
    }

    @Override
    public DispatcherType getDispatcherType() {
        return null;
    }

    @Override
	public long getContentLengthLong() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public String changeSessionId() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	private static class InputStream extends ServletInputStream {
        /**
         * The data. Is <code>null</code> if there is no data.
         */
        private final ByteArrayInputStream _stream;

        /**
         * Constructs a new <code>InputStream</code> instance for the specified
         * data.
         *
         * @param data the data, as a string, can be <code>null</code>.
         */
        private InputStream(String data) {
            String encoding = "ISO-8859-1";
            try {
                byte[] dataAsByte = data.getBytes(encoding);
                _stream = new ByteArrayInputStream(dataAsByte);
            } catch (UnsupportedEncodingException exception) {
                throw new RuntimeException("Failed to convert characters to bytes using encoding \"" + encoding + "\".");
            }
        }

        @Override
		public boolean isFinished() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isReady() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void setReadListener(ReadListener readListener) {
			// TODO Auto-generated method stub
			
		}

		public int read() throws IOException {
            return _stream.read();
        }

        public int read(byte[] b) throws IOException {
            return _stream.read(b);
        }

        public int read(byte[] b, int off, int len) throws IOException {
            return _stream.read(b, off, len);
        }

        public boolean markSupported() {
            return _stream.markSupported();
        }

        public void mark(int readlimit) {
            _stream.mark(readlimit);
        }

        public long skip(long n) throws IOException {
            return _stream.skip(n);
        }

        public void reset() throws IOException {
            _stream.reset();
        }

        public void close() throws IOException {
            _stream.close();
        }
    }
}
