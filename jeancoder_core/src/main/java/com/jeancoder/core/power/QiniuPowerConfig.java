package com.jeancoder.core.power;

public class QiniuPowerConfig extends PowerConfig {
	private String accessKey;
	private String secretKey;
	private String defaultBucket;
	public String getAccessKey() {
		return accessKey;
	}
	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}
	public String getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	public String getDefaultBucket() {
		return defaultBucket;
	}
	public void setDefaultBucket(String defaultBucket) {
		this.defaultBucket = defaultBucket;
	}
}
