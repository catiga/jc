package com.jeancoder.jdbc.sql;

public class GroupSqlFragment extends SqlFragment {

	String alias;
	
	public GroupSqlFragment() {
		super();
	}
	
	public String getAlias() {
		return alias;
	}
	
	public GroupSqlFragment(String action, String target) {
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
