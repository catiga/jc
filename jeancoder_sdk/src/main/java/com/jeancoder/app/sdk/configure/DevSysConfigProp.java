package com.jeancoder.app.sdk.configure;

import com.jeancoder.core.configure.PropItem;
import com.jeancoder.core.configure.PropType;

/**
 * 获取系统配置信息
 * @author huangjie
 *
 */
public class DevSysConfigProp extends PropItem {

	public DevSysConfigProp() {
		super(PropType.SYS);
	}
	private String checkUri;
	private String zipUri;
	private String loadUri;
	private String downloadUri;
	
	public String getCheckUri() {
		return checkUri;
	}
	public void setCheckUri(String checkUri) {
		this.checkUri = checkUri;
	}
	public String getZipUri() {
		return zipUri;
	}
	public void setZipUri(String zipUri) {
		this.zipUri = zipUri;
	}
	public String getDownloadUri() {
		return downloadUri;
	}
	public void setDownloadUri(String downloadUri) {
		this.downloadUri = downloadUri;
	}
	public String getLoadUri() {
		return loadUri;
	}
	public void setLoadUri(String loadUri) {
		this.loadUri = loadUri;
	}
}
