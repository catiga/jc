package com.jeancoder.root.env;

import java.io.Serializable;

@SuppressWarnings("serial")
public class JCAPP implements Serializable {

	String id;
	
	String code;
	
	String name;
	
	String dever;
	
	String org;
	
	String ver;
	
	String install_id;
	
	String ver_id;
	
	String source_type;
	
	String lans;
	
	String logbase;
	
	String app_base = null;
	
	String dyc_base = ".";
	
	String sta_base = "static";
	
	String tpl_base = "template";
	
	String lib_base = "lib";
	
	String bin_base;
	
	String source_base = "src/main/java";
	
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
		if(install_id!=null) {
			return app_base + "/" + install_id;
		}
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

	public String getBin_base() {
		return bin_base;
	}

	public void setBin_base(String bin_base) {
		this.bin_base = bin_base;
	}

	public String getSource_base() {
		return source_base;
	}

	public void setSource_base(String source_base) {
		this.source_base = source_base;
	}

	public String getInstall_id() {
		return install_id;
	}

	public void setInstall_id(String install_id) {
		this.install_id = install_id;
	}

	public String getVer_id() {
		return ver_id;
	}

	public void setVer_id(String ver_id) {
		this.ver_id = ver_id;
	}

}
