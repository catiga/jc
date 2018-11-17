package com.jeancoder.jdbc.sql;

public class SqlFragment {

	String action;
	String target;
	
	SqlFragment bepart;
	
	public SqlFragment() {}
	
	public SqlFragment(String action, String target) {
		this.action = action;
		this.target = target;
	}
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}

	public SqlFragment getBepart() {
		return bepart;
	}

	public void setBepart(SqlFragment bepart) {
		this.bepart = bepart;
	}
	
}
