package com.jeancoder.root.env;

public class JCAPP {

	String id;
	
	String code;
	
	String name;
	
	String dever;
	
	String org;
	
	String ver;
	
	String source_type;
	
	String lans;
	
	String logbase;
	
	String app_base = null;
	
	String dyc_base = ".";
	
	String sta_base = "static";
	
	String tpl_base = "template";
	
	String lib_base = "lib";
	
	JCAppConfig config;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDever() {
		return dever;
	}

	public void setDever(String dever) {
		this.dever = dever;
	}

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public String getSource_type() {
		return source_type;
	}

	public void setSource_type(String source_type) {
		this.source_type = source_type;
	}

	public String getLans() {
		return lans;
	}

	public void setLans(String lans) {
		this.lans = lans;
	}

	public String getLogbase() {
		return logbase;
	}

	public void setLogbase(String logbase) {
		this.logbase = logbase;
	}

	public String getApp_base() {
		return app_base;
	}

	public void setApp_base(String app_base) {
		this.app_base = app_base;
	}

	public String getDyc_base() {
		return dyc_base;
	}

	public void setDyc_base(String dyc_base) {
		this.dyc_base = dyc_base;
	}

	public String getSta_base() {
		return sta_base;
	}

	public void setSta_base(String sta_base) {
		this.sta_base = sta_base;
	}

	public String getTpl_base() {
		return tpl_base;
	}

	public void setTpl_base(String tpl_base) {
		this.tpl_base = tpl_base;
	}

	public String getLib_base() {
		return lib_base;
	}

	public void setLib_base(String lib_base) {
		this.lib_base = lib_base;
	}

	public JCAppConfig getConfig() {
		return config;
	}

	public void setConfig(JCAppConfig config) {
		this.config = config;
	}

}
