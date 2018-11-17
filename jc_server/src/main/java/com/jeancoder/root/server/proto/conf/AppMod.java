package com.jeancoder.root.server.proto.conf;

import com.jeancoder.root.container.model.JCAPP;

public class AppMod {

	String app_id;
	
	String app_code;
	
	String app_name;
	
	String dev_name;
	
	String org_name;
	
	String app_ver;
	
	String source_type;
	
	String lans;

	public String getApp_id() {
		return app_id;
	}

	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}

	public String getApp_code() {
		return app_code;
	}

	public void setApp_code(String app_code) {
		this.app_code = app_code;
	}

	public String getApp_name() {
		return app_name;
	}

	public void setApp_name(String app_name) {
		this.app_name = app_name;
	}

	public String getDev_name() {
		return dev_name;
	}

	public void setDev_name(String dev_name) {
		this.dev_name = dev_name;
	}

	public String getOrg_name() {
		return org_name;
	}

	public void setOrg_name(String org_name) {
		this.org_name = org_name;
	}

	public String getApp_ver() {
		return app_ver;
	}

	public void setApp_ver(String app_ver) {
		this.app_ver = app_ver;
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
	
	public JCAPP to() {
		JCAPP jcapp = null;
		if(this!=null) {
			jcapp = new JCAPP();
			jcapp.setCode(this.getApp_code());
			jcapp.setDever(this.getDev_name());
			jcapp.setId(this.getApp_id());
			jcapp.setLans(this.getLans());
			jcapp.setName(this.getApp_name());
			jcapp.setOrg(this.getOrg_name());
			jcapp.setSource_type(this.getSource_type());
			jcapp.setVer(this.getApp_ver());
		}
		return jcapp;
	}
}
