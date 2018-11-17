package com.jeancoder.core.namer;

/**
 * 应用对象
 * @author wow zhang_gh@cpis.cn
 * @date 2018年5月28日
 */
public class NamerApplication {
	//唯一识别码 
	private String appId;
	//中文名称
	private String appName;
	//英文名称 唯一
	private String appCode;
	//开发code
	private String developerCode;
	//编程语言
	private DevLang devLang;
	//获取方式
	private FetchWay fetchWay;
	//获取地址
	private String fetchAddress;
	//载入位置
	private InstallWay installWay;
	//载入地址
	private String installAddress;
	//描述信息
	private String describe;
	//默认首页可以为空
	private String index;
	
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getAppCode() {
		return appCode;
	}
	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}
	public DevLang getDevLang() {
		return devLang;
	}
	public void setDevLang(DevLang devLang) {
		this.devLang = devLang;
	}
	public FetchWay getFetchWay() {
		return fetchWay;
	}
	public void setFetchWay(FetchWay fetchWay) {
		this.fetchWay = fetchWay;
	}
	public String getFetchAddress() {
		return fetchAddress;
	}
	public void setFetchAddress(String fetchAddress) {
		this.fetchAddress = fetchAddress;
	}
	public InstallWay getInstallWay() {
		return installWay;
	}
	public void setInstallWay(InstallWay installWay) {
		this.installWay = installWay;
	}
	public String getDeveloperCode() {
		return developerCode;
	}
	public void setDeveloperCode(String developerCode) {
		this.developerCode = developerCode;
	}
	public String getInstallAddress() {
		return installAddress;
	}
	public void setInstallAddress(String installAddress) {
		this.installAddress = installAddress;
	}
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
}
