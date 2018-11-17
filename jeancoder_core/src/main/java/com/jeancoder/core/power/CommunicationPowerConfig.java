package com.jeancoder.core.power;

/**
 * 调用其他APP操作器配置
 * @author wow zhang_gh@cpis.cn
 * @date 2018年6月8日
 */
public class CommunicationPowerConfig extends PowerConfig{
	private CommunicationWorkMode mode;
	private String domain;
	private Integer deploy = 0;
	
	public CommunicationWorkMode getMode() {
		return mode;
	}
	public void setMode(CommunicationWorkMode mode) {
		this.mode = mode;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public Integer getDeploy() {
		return deploy;
	}
	public void setDeploy(Integer deploy) {
		this.deploy = deploy;
	}
	
}
