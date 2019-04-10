package com.jc.proto.msg.paramdebug;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ParamMod implements Serializable {

	Long totalCachedMaxSize = 50*1024*1024L;	//default value
	
	Long internalTimeout = 0L;					//default value, not handled

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
	
}
