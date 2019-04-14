package com.jc.proto.msg.ctparam;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CtParamMod implements Serializable {

	Long req_handle_timeout = 0L; // default handle timeout setting

	Long vs_queue_len = 20 * 1024 * 1024L; // 默认队列长度

	Integer vs_switch = 0; // 记录上报开关，默认为关

	public Long getReq_handle_timeout() {
		return req_handle_timeout;
	}

	public void setReq_handle_timeout(Long req_handle_timeout) {
		this.req_handle_timeout = req_handle_timeout;
	}

	public Long getVs_queue_len() {
		return vs_queue_len;
	}

	public void setVs_queue_len(Long vs_queue_len) {
		this.vs_queue_len = vs_queue_len;
	}

	public Integer getVs_switch() {
		return vs_switch;
	}

	public void setVs_switch(Integer vs_switch) {
		this.vs_switch = vs_switch;
	}

}
