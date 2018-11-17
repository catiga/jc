package com.jeancoder.core.resource.proc;

import java.util.ArrayList;

import com.jeancoder.core.exception.SdkRuntimeException;
import com.jeancoder.core.log.JCLogger;
import com.jeancoder.core.log.JCLoggerFactory;
import com.jeancoder.core.namer.MemoryPool;
import com.jeancoder.core.resource.type.ResourceType;
import com.jeancoder.core.util.MD5Util;

@SuppressWarnings("serial")
public class InterceptorGroovyMemoryDynamicResource extends InterceptorGroovyDynamicResource implements MemoryDynamicResource {
	
	private static final JCLogger LOGGER = JCLoggerFactory.getLogger(InterceptorGroovyDynamicResource.class.getName());
	
	private String appCode;
	
	public InterceptorGroovyMemoryDynamicResource(String rescontent, ResourceType restype) {
		super(rescontent, restype);
	}
	
	/**
	 * setResId() 之后再执行
	 * rescontent 存的是脚本内容
	 */
	@Override
	public void init () {
		mapping = new ArrayList<>();
		exmapping = new ArrayList<>();
		String[] liness = MemoryPool.getResource(appCode, getRescontent()).split("\r\n");
		for (String line : liness) {
			line = line.trim();
			if(line.startsWith("@urlmapped")) {
				line = line.substring("@urlmapped(".length(), line.indexOf(")")).trim();
				for(String t : line.split(",")) {
					t = t.trim();
					t = super.disposeFirst(t, "[");
					t = super.disposeLast(t, "]");
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
		if(mapping.isEmpty()) {
			//默认全部需要执行
			mapping.add("/");
		}
		mapping.forEach(it -> LOGGER.debug(it));
		exmapping.forEach(it -> LOGGER.debug(it));
		this.setPacode(MD5Util.getStringMD5(getResId()));
		this.setCtcode(MD5Util.getStringMD5(MemoryPool.getResource(appCode, getRescontent())));
	}
	
	@Override
	public Object getResult(){
		try{
			return this.getShell().evaluate(MemoryPool.getResource(appCode, getRescontent()));
		} catch (Exception e) {
			e.printStackTrace();
			throw new SdkRuntimeException("Rescontentid=" + getResId(), e);
		}
	}
	
	@Override
	public String getAppCode() {
		return appCode;
	}

	@Override
	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}
}
