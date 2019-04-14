package com.jeancoder.root.server.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jc.proto.msg.ctparam.CtParamMod;
import com.jc.proto.msg.paramdebug.ParamMod;
import com.jeancoder.core.util.JackSonBeanMapper;

public class GlobalStateHolder {
	
	protected static Logger logger = LoggerFactory.getLogger(GlobalStateHolder.class);

	private Long internalExecuteTimeout = 0L;	//UNLIMITED
	
//	private Long MAX_SIZE = 50*1024*1024L;	//50MB	最大缓存数据
//	
//	private Long MIN_SIZE = 10*1024L;	//50K	一次上传的最小数据
//	
//	private Long upTimeDiff = 5*60*1000L;
	
	private Integer vsSwitch = 0;	
	
	final static public GlobalStateHolder INSTANCE = new GlobalStateHolder();
	
	private GlobalStateHolder() {}
	
	@Deprecated
	public void reset(ParamMod mod) {
		logger.debug("ACCP_REPAR:" + JackSonBeanMapper.toJson(mod));
		if(mod!=null) {
			if(mod.getInternalTimeout()!=null&&mod.getInternalTimeout()>=0l) {
				this.internalExecuteTimeout = mod.getInternalTimeout();
			}
		}
	}
	
	public void resetNT(CtParamMod mod) {
		if(mod!=null) {
			this.vsSwitch = mod.getVs_switch();
			this.internalExecuteTimeout = mod.getReq_handle_timeout();
		}
	}
	
	public Long getInternalExecuteTimeout() {
		if(this.internalExecuteTimeout==null||this.internalExecuteTimeout<0L) {
			return 0L;
		}
		return this.internalExecuteTimeout;
	}

	public Integer getVsSwitch() {
		if(vsSwitch==null) {
			vsSwitch = 0;
		}
		if(!vsSwitch.equals(0) && !vsSwitch.equals(1)) {
			vsSwitch = 0;
		}
		return vsSwitch;
	}

	public void setInternalExecuteTimeout(Long internalExecuteTimeout) {
		if(internalExecuteTimeout!=null && internalExecuteTimeout>=0l) {
			this.internalExecuteTimeout = internalExecuteTimeout;
		} else {
			logger.error("EX_TIMEOUT PARAM NOT OK.");
		}
	}

	public void setVsSwitch(Integer vsSwitch) {
		if(vsSwitch!=null && (vsSwitch.equals(0) || vsSwitch.equals(1))) {
			this.vsSwitch = vsSwitch;
		} else {
			logger.error("VSSWITCHER PARAM NOT OK.");
		}
	}
}
