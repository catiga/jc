package com.jc.proto.conf.difv;

import java.io.Serializable;

import com.jc.proto.conf.APPConf;
import com.jc.proto.conf.AppMod;
import com.jeancoder.root.env.JCAPP;
import com.jeancoder.root.env.JCAppConfig;

@SuppressWarnings("serial")
public class AppMod30Up extends AppMod implements Serializable {
	
	String av_install_id;
	
	String nv_id;
	
	public String getAv_install_id() {
		return av_install_id;
	}

	public void setAv_install_id(String av_install_id) {
		this.av_install_id = av_install_id;
	}

	public String getNv_id() {
		return nv_id;
	}

	public void setNv_id(String nv_id) {
		this.nv_id = nv_id;
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
			jcapp.setInstall_id(this.getAv_install_id());
			jcapp.setVer_id(this.getNv_id());
			jcapp.setApp_base(this.getApp_base());
			jcapp.setConfig(this.to(this.getConfig()));
		}
		return jcapp;
	}
	
	public JCAppConfig to(APPConf confinfo) {
		JCAppConfig config = null;
		if(confinfo!=null) {
			config = new JCAppConfig();
			config.setDescription(confinfo.getDescription());
			config.setIndex(confinfo.getIndex());
		}
		return config;
	}
}
