package com.jeancoder.jdbc.sql;

public class UpdateSqlFragment extends SqlFragment {

	public UpdateSqlFragment() {
		super();
	}
	
	public UpdateSqlFragment(String action, String target) {
		super(action, target);
	}
	
	public void replace(String table_name) {
		this.target = table_name;
	}
}
