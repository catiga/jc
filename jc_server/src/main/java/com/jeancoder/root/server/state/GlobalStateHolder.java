package com.jeancoder.root.server.state;

import com.jc.proto.msg.paramdebug.ParamMod;

public class GlobalStateHolder {

	private Long internalExecuteTimeout = 0L;	//UNLIMITED
	
	private Long MAX_SIZE = 50*1024*1024L;	//50MB
	
	private Long MIN_SIZE = 1*1024*1024L;
	
	final static public GlobalStateHolder INSTANCE = new GlobalStateHolder();
	
	private GlobalStateHolder() {}
	
	public void reset(ParamMod mod) {
		if(mod!=null) {
			if(mod.getInternalTimeout()!=null&&mod.getInternalTimeout()>=0l) {
				this.internalExecuteTimeout = mod.getInternalTimeout();
			}
			
			if(mod.getTotalCachedMaxSize()!=null && mod.getTotalCachedMaxSize()>=0l) {
				this.MAX_SIZE = mod.getTotalCachedMaxSize();
			}
			if(mod.getTotalCachedMinSize()!=null && mod.getTotalCachedMinSize()>0l) {
				if(mod.getTotalCachedMinSize() < mod.getTotalCachedMaxSize()) {
					this.MIN_SIZE = mod.getTotalCachedMinSize();
				}
			}
		}
	}
	
	public Long inExCallTimeout() {
		if(this.internalExecuteTimeout==null||this.internalExecuteTimeout<0L) {
			return 0L;
		}
		return this.internalExecuteTimeout;
	}
	
	public Long cachedMaxSize() {
		if(this.MAX_SIZE==null||this.MAX_SIZE<0L) {
			return 0L;		//not cached;
		}
		return this.MAX_SIZE;
	}
	
	public Long cachedMinSize() {
		if(this.MIN_SIZE==null||this.MIN_SIZE<0L) {
			return 0L;		//not cached;
		}
		return this.MIN_SIZE;
	}
}
