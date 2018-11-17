package com.jeancoder.core.resource.proc;

import com.jeancoder.core.resource.type.ResourceType;

public interface Resource {

	/**
	 * 直接读取资源字符串
	 * @return
	 */
	String getRescontent();
	
	ResourceType getRestype();
	
	String getResId();
	
	/**
	 * 运行时返回的资源 
	 * @return
	 */
	Object getResult();
}
