package com.jc.proto.conf;

public class InsPerf {

	Long req_handle_timeout = 0L;	//default handle timeout setting
	
	Integer vs_switch = 0;			//记录上报开关，默认为关

	public Long getReq_handle_timeout() {
		return req_handle_timeout;
	}

	public Integer getVs_switch() {
		return vs_switch;
	}
	
}
