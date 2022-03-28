package com.jeancoder.jdbc.sql;

import java.util.ArrayList;
import java.util.List;

import com.jeancoder.jdbc.JCEEMapper;
import com.jeancoder.jdbc.bean.JCBean;

public class SqlParser {
	
	String originalSql;
	
	String formatSql;
	
	List<SqlFragment> frags;
	
	public String getOriginalSql() {
		return originalSql;
	}

	public String getFormatSql() {
		return formatSql;
	}

	public List<SqlFragment> getFrags() {
		return frags;
	}

	public SqlParser(String input) {
		this.originalSql = input;
		formatSql = this.format(input);
		frags = this.build2();
	}
	
	protected List<SqlFragment> build2() {
		String[] frs = formatSql.split(" ");
		List<SqlFragment> ret = new ArrayList<SqlFragment>();
		int i = 0;
		while(i<frs.length) {
			StringBuffer actions = new StringBuffer();
			i = indexKeys(i, frs, actions);
			String action = actions.toString().trim();
			
			StringBuffer targbuf = new StringBuffer();
			int next_j = i;
			for(;next_j<frs.length; next_j++) {
				String tmp_frs = frs[next_j];
				String preContext = null;
				if(next_j>0) {
					preContext = frs[next_j - 1];
				}
				if(isKeyWord(preContext, tmp_frs)) {
					break;
				}
				targbuf.append(tmp_frs + " ");
			}
			SqlFragment frag = SqlFragmentFactory.generate(action, targbuf.toString().trim());
			ret.add(frag); i = next_j;
		}
		return ret;
	}
	
	protected int indexKeys(int i, String[] frs, StringBuffer actions) {
		String preContext = null;
		if(i>0) {
			preContext = frs[i-1];
		}
		if(i>=frs.length||!isKeyWord(preContext, frs[i].trim())) { return i; }
		String tmp = frs[i].trim();
		actions.append(" " + tmp.toUpperCase());
		return indexKeys(i+1, frs, actions);
	}
	
	public static void main(String[] argc) {
		String sql = "select c.city,sum(c.cnt) cnt from (select concat(e.province,'-',e.city) city,count(1) cnt from cx_enterprise e where e.state in (1) group by concat(e.province,'-',e.city))";
		SqlParser par = new SqlParser(sql);
		System.out.println(par.clearSql());
		
	}
	
	
	
	
	
	protected String format(String input) {
		if(input==null||input.trim().equals("")) return null;
		input = input.trim();
		String[] ftsql = input.split(" ");
		StringBuffer sqlbuffer = new StringBuffer();
		for(String s : ftsql) {
			String litok = null;
			if(!(s.equals("")||s.equals("\n")||s.equalsIgnoreCase("\r"))) {
				litok = s;
			}
			if(litok!=null) {
				if(litok.endsWith(",")||litok.endsWith("(")) {
					sqlbuffer.append(litok.substring(0, litok.length() - 1) + " " + litok.substring(litok.length() - 1) + " ");
				} else {
					sqlbuffer.append(litok + " ");
				}
			}
		}
		return sqlbuffer.toString().trim();
	}
	
	protected boolean isKeyWord(String s) {
		s = s.toUpperCase();
		for(Token t : Token.values()) {
			if(s.equals(t.name)) {
				return true;
			}
		}
		for(Symbol t : Symbol.values()) {
			if(!s.equals("*")&&s.startsWith(t.name)) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean isKeyWord(String preContext, String s) {
		if(preContext!=null) {
			if(preContext.equalsIgnoreCase("FROM") || preContext.equalsIgnoreCase("UPDATE")) {
				return false;
			}
		}
		s = s.toUpperCase();
		for(Token t : Token.values()) {
			if(s.equals(t.name)) {
				return true;
			}
		}
		for(Symbol t : Symbol.values()) {
			if(!s.equals("*")&&s.startsWith(t.name)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isSelectSql() {
		SqlFragment start = (frags!=null&&!frags.isEmpty()?frags.get(0):null);
		if(start!=null&&(start instanceof SelectSqlFragment)) {
			return true;
		}
		return false;
	}
	
	
	public String clearSql() {
		List<SqlFragment> frags = this.getFrags();
		StringBuffer buf = new StringBuffer();
		
		for(SqlFragment sf : frags) {
			if(sf instanceof FromSqlFragment) {
				String[] targets = sf.getTarget().split(",");
				StringBuffer from_buf = new StringBuffer();
				for(String s : targets) {
					String[] arr_s = s.split(" ");
					int tmpi = 0;
					for(String t : arr_s) {
						if(tmpi==0) {
							String tbname = JCEEMapper.getInstance().__(t, JCBean.class);
							if(tbname==null) {
								tbname = t;
							}
							from_buf.append(tbname);
						} else {
							from_buf.append(" " + t);
						}
					}
					from_buf.append(" ");
					if(((FromSqlFragment)sf).getAlias()!=null) {
						from_buf.append(((FromSqlFragment)sf).getAlias());
					}
				}
				((FromSqlFragment)sf).replace(from_buf.toString().trim());
			} else if(sf instanceof UpdateSqlFragment) {
				String tbname = null;
				tbname = JCEEMapper.getInstance().__(sf.getTarget(), JCBean.class);
				if(tbname!=null) {
					((UpdateSqlFragment)sf).replace(tbname);
				}
			}
			buf.append(sf.getAction() + " " + sf.getTarget() + " ");
		}
		
		return buf.toString().trim();
	}
	
	public String toCountSql() {
		if(!isSelectSql()) {
			return null;
		}
		if(isGroupBySql()) {
			return toCountGroupSql();
		}
		SqlFragment select = frags.get(0);
		select.target = "count(*)";
		StringBuffer buf = new StringBuffer();
		buf.append(select.action + " " + select.target + " ");
		boolean allow_append = false;
		for(int i=1; i<frags.size(); i++) {
			if(frags.get(i).action.equals("limit")) {
				continue;
			}
			if((frags.get(i).getAction().equals(Token.FROM.name)) && (!allow_append)) {
				allow_append = true;
			}
			if(allow_append) {
				buf.append(frags.get(i).action + " " + frags.get(i).target + " ");
			}
		}
		return buf.toString().trim();
	}
	
	public boolean isGroupBySql() {
		boolean ret = false;
		for(SqlFragment x : this.frags) {
			if(x instanceof GroupSqlFragment) {
				ret = true;
				break;
			}
		}
		return ret;
	}
	
	public String toCountGroupSql() {
		StringBuffer buf = new StringBuffer();
		buf.append("SELECT count(*) FROM (");
		for(int i=0; i<frags.size(); i++) {
			if(frags.get(i).action.equals("limit")) {
				continue;
			}
			buf.append(frags.get(i).action + " " + frags.get(i).target + " ");
		}
		buf.append(") __A__");
		return buf.toString().trim();
	}
	
	
	
	
	
	protected List<SqlFragment> build() {
		String[] frs = formatSql.split(" ");
		List<SqlFragment> ret = new ArrayList<SqlFragment>();
		int i = 0;
		while(i<frs.length) {
			String s = frs[i];
			if(isKeyWord(s)) {
				String action = s.toUpperCase();
				if(action.equals(Token.GROUP.name)||action.equals(Token.ORDER.name)||action.equals(Token.INSERT.name)) {
					action = action + " " + frs[++i].toUpperCase();
				}
				StringBuffer targbuf = new StringBuffer();
				int next_j = 0;
				for(next_j=i+1; next_j<frs.length; next_j++) {
					String tmp_frs = frs[next_j];
					if(next(action, tmp_frs)) {
						//targbuf.append(frs[next_j].toUpperCase() + " ");
						targbuf.append(frs[next_j] + " ");
						continue;
					}
					if(isKeyWord(tmp_frs)) {
						break;
					}
					targbuf.append(frs[next_j] + " ");
				}
				SqlFragment frag = SqlFragmentFactory.generate(action, targbuf.toString().trim());
				ret.add(frag); i = next_j;
			}
		}
		return ret;
	}
	
	protected boolean next(String action, String tmp_frs) {
		if(action.equals(Token.ORDER.name + " " + Token.BY.name)) {
			if(tmp_frs.toUpperCase().equals(Token.ASC.name)
					||tmp_frs.toUpperCase().equals(Token.DESC.name)
					||tmp_frs.toUpperCase().equals(Symbol.COMMA.name)) {
				return true;
			}
		} else if(action.equals(Token.SELECT.name)) {
			if(tmp_frs.toUpperCase().equals(Symbol.COMMA.name)) {
				return true;
			}
		}
		return false;
	}
	
}
