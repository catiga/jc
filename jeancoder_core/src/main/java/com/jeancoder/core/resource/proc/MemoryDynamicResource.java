package com.jeancoder.core.resource.proc;

public interface MemoryDynamicResource {
	
	// 缓存中的资源 则应该存储appcode，以方便查找到原位置
	public String getAppCode();
	public void setAppCode(String appCode);
}
