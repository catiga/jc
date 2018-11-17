package com.jeancoder.core.resource.proc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.jeancoder.core.exception.SdkRuntimeException;
import com.jeancoder.core.log.JCLogger;
import com.jeancoder.core.log.JCLoggerFactory;
import com.jeancoder.core.resource.type.InterceptorDynamicResourceType;
import com.jeancoder.core.resource.type.ResourceType;
import com.jeancoder.core.util.MD5Util;

@SuppressWarnings("serial")
public class InterceptorGroovyDynamicResource extends GroovyDynamicResource {
	
	private static final JCLogger LOGGER = JCLoggerFactory.getLogger(InterceptorGroovyDynamicResource.class.getName());
	
	String pacode;
	
	String ctcode;

	//这里为需要执行此拦截器的规则
	protected List<String> mapping;
	//这里为不需要执行此拦截器的规则
	protected List<String> exmapping;
	
	public static void main(String[] argc) {
		new InterceptorGroovyDynamicResource("/Users/jackielee/Documents/dev_workspace/92yp_app_project/src/main/java/com/jeancoder/project/interceptor/permission/test.groovy", new InterceptorDynamicResourceType());
	}
	
	public InterceptorGroovyDynamicResource(String rescontent, ResourceType restype) {
		super(rescontent, restype);
	}
	
	/**
	 * setResId() 之后再执行
	 * rescontent 存的是脚本文件路径
	 */
	public void init () {
		mapping = new ArrayList<>();
		exmapping = new ArrayList<>();
		try (Stream<String> stream = Files.lines(Paths.get(getRescontent()))) {
			stream.forEach(
				line -> {
					line = line.trim();
					if(line.startsWith("@urlmapped")) {
						line = line.substring("@urlmapped(".length(), line.indexOf(")")).trim();
						for(String t : line.split(",")) {
							t = t.trim();
							t = disposeFirst(t, "[");
							t = disposeLast(t, "]");
							mapping.add(t.substring(1, t.length() - 1));
						}
					} else if(line.startsWith("@urlpassed")) {
						line = line.substring("@urlpassed(".length(), line.indexOf(")")).trim();
						for(String t : line.split(",")) {
							t = t.trim();
							t = disposeFirst(t, "[");
							t = disposeLast(t, "]");
							exmapping.add(t.substring(1, t.length() - 1));
						}
					}
				}
			);
		}catch (IOException e) {
			LOGGER.error("", e);
		}
		if(mapping.isEmpty()) {
			//默认全部需要执行
			mapping.add("/");
		}
		mapping.forEach(it -> LOGGER.debug(it));
		exmapping.forEach(it -> LOGGER.debug(it));
		this.setPacode(MD5Util.getStringMD5(getResId()));
		this.setCtcode(MD5Util.getFileMD5(this.getRescontent()));
	}
	
	@Override
	public Object getResult(){
		try{
			File file = new File(getRescontent());
			return this.getShell().evaluate(file);
		} catch (Exception e) {
			throw new SdkRuntimeException("", e);
		}
	}
	
	public Object getResult(String uri) {
		uri = disposeLast(uri, "/");
		//默认设置为不需要执行，也就是说当expass=true时候，才需要执行
		boolean expass = false;
		//first check mapping
		if(mapping!=null) {
			//首先确认是否需要执行
			for(String s : mapping) {
				if(uri.startsWith(s)) {
					expass = true;
					break;
				}
			}
		}
		//second check 是否允许放过
		if(expass) {
			if(exmapping!=null) {
				for(String s : exmapping) {
					if(uri.startsWith(s)) {
						expass = false;
						break;
					}
				}
			}
		}
		//默认为真
		Object result = true;
		if(expass) {
			result = this.getResult();
		}
		return result;
	}
	
	public String getPacode() {
		return pacode;
	}

	public void setPacode(String pacode) {
		this.pacode = pacode;
	}

	public String getCtcode() {
		return ctcode;
	}

	public void setCtcode(String ctcode) {
		this.ctcode = ctcode;
	}

	protected static String disposeLast(String uri, String charac) {
		uri = uri.trim();
		if(uri.equals(charac)) {
			return uri;
		}
		if(uri.endsWith(charac)) {
			uri = uri.substring(0, uri.length() - 1);
			if(uri.endsWith(charac)) {
				uri = disposeLast(uri, charac);
			}
		}
		return uri;
	}
	
	protected static String disposeFirst(String uri, String charac) {
		uri = uri.trim();
		if(uri.equals(charac)) {
			return uri;
		}
		if(uri.startsWith(charac)) {
			uri = uri.substring(1, uri.length());
			if(uri.startsWith(charac)) {
				uri = disposeFirst(uri, charac);
			}
		}
		return uri;
	}
	
	
}
