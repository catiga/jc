package com.jeancoder.jdbc.sql;

public class SqlFragmentFactory {

	public static SqlFragment generate(String action, String target) {
		SqlFragment aim = null;
		if(action.equals(Token.SELECT.name)) {
			aim = new SelectSqlFragment(action, target);
		} else if(action.equals(Token.FROM.name)) {
			aim = new FromSqlFragment(action, target);
		} else if(action.equals(Token.UPDATE.name)) {
			aim = new UpdateSqlFragment(action, target);
		} else if(action.equals(Symbol.COMMA.name)) {
			aim = new CommaSqlFragment(action, target);
		} else if(action.indexOf(Token.JOIN.name)>-1) {
			aim = new JoinSqlFragment(action, target);
		} else if(action.indexOf(Token.DELETE.name)>-1 && action.indexOf(Token.FROM.name)>0) {
			aim = new DeleteFromSqlFragment(action, target);
		}
		if(aim==null) {
			aim = new SqlFragment(action, target);
		}
		return aim;
	}
}
