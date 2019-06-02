package com.jc.proto.conf;

import java.io.Serializable;

import com.jeancoder.root.env.JCAPP;
import com.jeancoder.root.env.JCAppConfig;

/**
 * Empty or Old Version App Message Exchanges
 * @author jackielee
 *
 */
@SuppressWarnings("serial")
public class AppMod implements Serializable {

	String app_id;
	
	String app_code;
	
	String app_name;
	
	String dev_name;
	
	String org_name;
	
	String app_ver;
	
	String source_type;
	
	String lans;
	
	String app_base;	//support folder and zip file
	
	String fetch_address;
	
	APPConf config;

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
	
	public  String getApp_base() {
		return app_base;
	}

	public void setApp_base(String app_base) {
		this.app_base = app_base;
	}

	protected APPConf getConfig() {
		return config;
	}

	protected void setConfig(APPConf config) {
		this.config = config;
	}
	
	
	public String getFetch_address() {
		return fetch_address;
	}

	public void setFetch_address(String fetch_address) {
		this.fetch_address = fetch_address;
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
			jcapp.setApp_base(this.getApp_base());
			jcapp.setConfig(this.to(this.config));
		}
		return jcapp;
	}
	
	public JCAppConfig to(APPConf confinfo) {
		JCAppConfig config = null;
		if(confinfo!=null) {
			config = new JCAppConfig();
			config.setDescription(confinfo.description);
			config.setIndex(confinfo.index);
		}
		return config;
	}
}
