package com.jeancoder.core.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class JCRequest {
	private static final int MEMORY_THRESHOLD   = 1024 * 1024 * 3;  // 3MB
    private static final int MAX_FILE_SIZE      = 1024 * 1024 * 40; // 40MB
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 50; // 50MB

	private HttpServletRequest request;
	
	private JCSession session;
	
	private JCCookie[] cookies = new JCCookie[0];
	
	private Map<String, String> _parameters_ = new HashMap<>();
	
	private List<FileItem> _file_items_ = null;
	
	public JCRequest(HttpServletRequest request) {
		this.request = request;
		session = new JCSession(request.getSession());
		if (request.getCookies() == null || request.getCookies().length == 0) {
			return;
		} 
		cookies = new JCCookie[request.getCookies().length];
		for (int i = 0; i < request.getCookies().length; i++) {
			cookies[i] = new JCCookie(request.getCookies()[i]);
		}
		
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if(isMultipart) {
			// 配置上传参数
	        DiskFileItemFactory factory = new DiskFileItemFactory();
	        // 设置内存临界值 - 超过后将产生临时文件并存储于临时目录中
	        factory.setSizeThreshold(MEMORY_THRESHOLD);
	        // 设置临时存储目录
	        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
	        ServletFileUpload upload = new ServletFileUpload(factory);
	        // 设置最大文件上传值
	        upload.setFileSizeMax(MAX_FILE_SIZE);
	        // 设置最大请求值 (包含文件和表单数据)
	        upload.setSizeMax(MAX_REQUEST_SIZE);
	        // 中文处理
	        upload.setHeaderEncoding("UTF-8"); 
	        
	        try {
				_file_items_ = upload.parseRequest(request);
				if(_file_items_!=null&&!_file_items_.isEmpty()) {
					Iterator<?> iter = _file_items_.iterator();
					while(iter.hasNext()){
						FileItem item = (FileItem)iter.next();
						if(item.isFormField()){
							item.isInMemory();
							String map_key = item.getFieldName();
							String map_value = item.getString();
							try {
								map_value = item.getString("UTF-8");
							} catch (UnsupportedEncodingException e) {
							}
							if(map_value!=null) {
								_parameters_.put(map_key, map_value);
							}
							iter.remove();
						}
					}
				}
			} catch (FileUploadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	public String getParameter(String arg0) {
		String value = request.getParameter(arg0);
		if(value==null) {
			value = _parameters_.get(arg0);
		}
		return value;
	}

	public Map<String, String[]> getParameterMap() {
		return request.getParameterMap();
	}
	public JCSession getSession() {
		return session;
	}
	
	public JCCookie[] getCookies() {
		return cookies;
	}
	
	public  String getContextPath() {
		return request.getContextPath();
	}
	
	public Object getAttribute(String name) {
		return request.getAttribute(name);
	}
	
	public void setAttribute(String name, Object value) {
		 request.setAttribute(name, value);
	}
	
	public String getPathInfo() {
		return request.getPathInfo();
	}
	
	public String getRequestURI() {
		return request.getRequestURI();
	}
	
	public StringBuffer getRequestURL() {
		return request.getRequestURL();
	}
	public int getServerPort() {
		return request.getServerPort();
	}
	
	public String getServerName() {
		return request.getServerName();
	}
	
	public String getRemoteHost() {
		return request.getRemoteHost();
	}
	
	public String getProtocol() {
		return request.getProtocol();
	}
	
	public String getServletPath() {
		return request.getServletPath();
	}
	
	public String getHeader(String name) {
		return request.getHeader(name);
	}
	
	public String getQueryString() {
		return request.getQueryString();
	}
	
	public InputStream getInputStream() {
		try {
			return request.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 当提交的form表单包含enctype="multipart/form-data" 属性时，可以使用此方法获取上传的文件列表
	 * @return
	 * @throws FileUploadException
	 */
//	public List<FileItem> getFormItems() throws FileUploadException {
//		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
//		if(!isMultipart) {
//			return null;
//		}
//		// 配置上传参数
//        DiskFileItemFactory factory = new DiskFileItemFactory();
//        // 设置内存临界值 - 超过后将产生临时文件并存储于临时目录中
//        factory.setSizeThreshold(MEMORY_THRESHOLD);
//        // 设置临时存储目录
//        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
//        ServletFileUpload upload = new ServletFileUpload(factory);
//        // 设置最大文件上传值
//        upload.setFileSizeMax(MAX_FILE_SIZE);
//        // 设置最大请求值 (包含文件和表单数据)
//        upload.setSizeMax(MAX_REQUEST_SIZE);
//        // 中文处理
//        upload.setHeaderEncoding("UTF-8"); 
//        return upload.parseRequest(request);
//	}
	
	public List<FileItem> getFormItems() {
		return _file_items_;
	}
	public Long getLong(String name) {
		Pattern p = Pattern.compile("^-?[0-9]*$");
		String stringValue = request.getParameter(name);
		if(stringValue != null && !"".equals(stringValue) && p.matcher(request.getParameter(name)).matches()) {
			return Long.valueOf(request.getParameter(name));
		}else {
			return null;
		}
	}
	public  String getRemoteAddr() {
		return request.getRemoteAddr();
	}
}
