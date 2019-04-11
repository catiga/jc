package com.jc.proto.msg.paramdebug;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ParamMod implements Serializable {

	Long totalCachedMaxSize = 50*1024*1024L;	//default value
	
	Long totalCachedMinSize = 1*1024*1024L;	//default value
	
	Long internalTimeout = 0L;					//default value, not handled
	
	Long upTimeDiff = 5*60*1000L;			//默认上传时间五分钟

	public Long getTotalCachedMaxSize() {
		return totalCachedMaxSize;
	}

	public void setTotalCachedMaxSize(Long totalCachedMaxSize) {
		this.totalCachedMaxSize = totalCachedMaxSize;
	}

	public Long getInternalTimeout() {
		return internalTimeout;
	}

	public void setInternalTimeout(Long internalTimeout) {
		this.internalTimeout = internalTimeout;
	}

	public Long getTotalCachedMinSize() {
		return totalCachedMinSize;
	}

	public void setTotalCachedMinSize(Long totalCachedMinSize) {
		this.totalCachedMinSize = totalCachedMinSize;
	}

	public Long getUpTimeDiff() {
		return upTimeDiff;
	}

	public void setUpTimeDiff(Long upTimeDiff) {
		this.upTimeDiff = upTimeDiff;
	}
	
}
