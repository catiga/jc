package com.jeancoder.jdbc.sql;

public class CommaSqlFragment extends FromSqlFragment {

	String alias;
	
	public CommaSqlFragment() {
		super();
	}
	
	public String getAlias() {
		return alias;
	}
	
	public CommaSqlFragment(String action, String target) {
		super(action, target);
		String[] arr_target = target.trim().split(" ");
		this.target = arr_target[0];
		int index = 0;
		for(String s : arr_target) {
			if(index++>0&&!s.trim().equals("")) {
				this.alias = s;
				break;
			}
		}
	}
	
	public void replace(String table_name) {
		this.target = table_name;
	}
}
