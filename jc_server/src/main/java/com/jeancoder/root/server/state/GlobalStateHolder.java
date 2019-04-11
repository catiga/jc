package com.jeancoder.root.server.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jc.proto.msg.paramdebug.ParamMod;
import com.jeancoder.core.util.JackSonBeanMapper;

public class GlobalStateHolder {
	
	protected static Logger logger = LoggerFactory.getLogger(GlobalStateHolder.class);

	private Long internalExecuteTimeout = 0L;	//UNLIMITED
	
	private Long MAX_SIZE = 50*1024*1024L;	//50MB	最大缓存数据
	
	private Long MIN_SIZE = 10*1024L;	//50K	一次上传的最小数据
	
	private Long upTimeDiff = 5*60*1000L;
	
	final static public GlobalStateHolder INSTANCE = new GlobalStateHolder();
	
	private GlobalStateHolder() {}
	
	public void reset(ParamMod mod) {
		logger.debug("ACCP_REPAR:" + JackSonBeanMapper.toJson(mod));
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
			if(mod.getUpTimeDiff()!=null && mod.getUpTimeDiff()>0l) {
				this.upTimeDiff = mod.getUpTimeDiff();
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
	
	public Long upTimeDiff() {
		return this.upTimeDiff;
	}
}
