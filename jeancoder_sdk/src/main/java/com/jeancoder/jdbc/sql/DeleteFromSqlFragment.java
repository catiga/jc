package com.jeancoder.jdbc.sql;

public class DeleteFromSqlFragment extends FromSqlFragment {

	String alias;
	
	public DeleteFromSqlFragment() {
		super();
	}
	
	public String getAlias() {
		return alias;
	}
	
	public DeleteFromSqlFragment(String action, String target) {
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
