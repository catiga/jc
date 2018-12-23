package com.jeancoder.jdbc.template;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.jeancoder.app.sdk.source.DatabaseSource;
import com.jeancoder.app.sdk.source.LoggerSource;
import com.jeancoder.core.exception.JeancoderException;
import com.jeancoder.core.log.JCLogger;
import com.jeancoder.core.power.DatabasePower;
import com.jeancoder.core.power.result.JeancoderResultSet;
import com.jeancoder.jdbc.JCME2SQL;
import com.jeancoder.jdbc.JcPage;
import com.jeancoder.jdbc.sql.SqlParser;

public class CommonJcDaoTemplate<T> extends GeneralJcDaoTemplate<T> {
	
	private static final JCLogger LOGGER = LoggerSource.getLogger(CommonJcDaoTemplate.class.getName());
	
	public int batchExecute(String sql, Object...params) {
		SqlParser par = this.buildSql(null, sql);
		sql = par.clearSql();
		DatabasePower dp = DatabaseSource.getDatabasePower();
		try {
			int code = dp.doUpdate(sql, params);
			return code;
		} catch (JeancoderException e) {
			LOGGER.error("", e);
		}
		return -1;
	}
	
	public <X> X save(T entity) {
		SqlParser par = JCME2SQL.generateInsert(entity);
		DatabasePower dp = DatabaseSource.getDatabasePower();
		String insert = par.getFormatSql();
		try {
			//int code = dp.doUpdate(insert);
			X code = dp.doInsert(insert);
			LOGGER.debug(code + "");
			return code;
		} catch (JeancoderException e) {
			LOGGER.error("class:" + entity.getClass().getName(), e);
			return null;
		}
	}
	
	public int update(T entity) {
		SqlParser par = JCME2SQL.fullUpdate(entity);
		DatabasePower dp = DatabaseSource.getDatabasePower();
		String insert = par.getFormatSql();
		try {
//			println(insert);
			int code = dp.doUpdate(insert);
			LOGGER.debug(code + "");
			return code;
		} catch (JeancoderException e) {
			LOGGER.error("class:" + entity.getClass().getName(), e);
			return -1;
		}
	}
	
	@SuppressWarnings("deprecation")
	public T get(Class<T> mapclass, String sql, Object...params) {
		T instance = null;
		DatabasePower dp = DatabaseSource.getDatabasePower();
		JeancoderResultSet jrs = null;
		try {
			SqlParser par = this.buildSql(mapclass, sql);
			sql = par.clearSql();
			sql = sql + " limit " + 0 + ", " + 1;
			jrs = dp.doQuery(sql, params);
			ResultSet rs = jrs.getResultSet();
			assert rs!=null;
			
			while(rs.next()) {
				instance = this.wrapperInstance(mapclass, rs);
			}
			
		} catch (Exception e) {
			LOGGER.error("jc_jdbc_template_error", e);
		}finally{
			if(jrs!=null)
				jrs.closeConnection();
		}
		return instance;
	}
	
	@SuppressWarnings("deprecation")
	public JcPage<T> find(Class<T> mapclass, JcPage<T> page, String sql, final Object... params) {
		DatabasePower dp = DatabaseSource.getDatabasePower();
		JeancoderResultSet jrs = null;
		try {
			Integer start = page.computeFirst();
			Integer end = page.getPs();
			SqlParser par = this.buildSql(mapclass, sql);
			sql = par.clearSql();
			sql = sql + " limit " + start + ", " + end;
			jrs = dp.doQuery(sql, params);
			ResultSet rs = jrs.getResultSet();
			assert rs!=null;
			
			List<T> ret = null;
			while(rs.next()) {
				if(ret==null) {
					synchronized(this) {
						if(ret==null) {
							ret = new LinkedList<T>();
						}
					}
				}
				T instance = this.wrapperInstance(mapclass, rs);
				ret.add(instance);
			}
			Long total_count = countSql(par, params);
			page.setTotalCount(total_count);
			page.setResult(ret);
			
		} catch (Exception e) {
			LOGGER.error("jc_jdbc_template_error", e);
		}finally{
			if(jrs!=null)
				jrs.closeConnection();
		}
		return page;
	}

	@SuppressWarnings("deprecation")
	public List<T> find(Class<T> mapclass, String sql, final Object... params) {
		DatabasePower dp = DatabaseSource.getDatabasePower();
		List<T> ret = null;
		JeancoderResultSet jrs = null;
		try {
			SqlParser par = this.buildSql(mapclass, sql);
			sql = par.clearSql();
			jrs = dp.doQuery(sql, params);
			ResultSet rs = jrs.getResultSet();
			assert rs!=null;
			
			while(rs.next()) {
				if(ret==null) {
					synchronized(this) {
						if(ret==null) {
							ret = new LinkedList<T>();
						}
					}
				}
				T instance = this.wrapperInstance(mapclass, rs);
				ret.add(instance);
			}
		} catch (Exception e) {
			LOGGER.error("jc_jdbc_template_error", e);
		}finally{
			if(jrs!=null)
				jrs.closeConnection();
		}
		return ret;
	}
	
	@SuppressWarnings("deprecation")
	public List<Object[]> findRawData(Class<T> mapclass, String sql, final Object... params) {
		DatabasePower dp = DatabaseSource.getDatabasePower();
		List<Object[]> ret = null;
		JeancoderResultSet jrs = null;
		try {
			SqlParser par = this.buildSql(mapclass, sql);
			sql = par.clearSql();
			jrs = dp.doQuery(sql, params);
			ResultSet rs = jrs.getResultSet();
			assert rs!=null;
			
			while(rs.next()) {
				if(ret==null) {
					synchronized(this) {
						if(ret==null) {
							ret = new ArrayList<Object[]>();
						}
					}
				}
				List<Object> instance = this.wrapperRawData(mapclass, rs);
				ret.add(instance.toArray());
			}
		} catch (Exception e) {
			LOGGER.error("jc_jdbc_template_error", e);
		}finally{
			if(jrs!=null)
				jrs.closeConnection();
		}
		return ret;
	}
	
	@SuppressWarnings("deprecation")
	public JcPage<Object[]> findRawData(Class<T> mapclass, JcPage<Object[]> page, String sql, final Object... params) {
		DatabasePower dp = DatabaseSource.getDatabasePower();
		JeancoderResultSet jrs = null;
		try {
			Integer start = page.computeFirst();
			Integer end = page.getPs();
			SqlParser par = this.buildSql(mapclass, sql);
			sql = par.clearSql();
			sql = sql + " limit " + start + ", " + end;
			jrs = dp.doQuery(sql, params);
			ResultSet rs = jrs.getResultSet();
			assert rs!=null;
			
			List<Object[]> ret = null;
			while(rs.next()) {
				if(ret==null) {
					synchronized(this) {
						if(ret==null) {
							ret = new ArrayList<Object[]>();
						}
					}
				}
				List<Object> instance = this.wrapperRawData(mapclass, rs);
				ret.add(instance.toArray());
			}
			Long total_count = countSql(par, params);
			page.setTotalCount(total_count);
			page.setResult(ret);
			
		} catch (Exception e) {
			LOGGER.error("jc_jdbc_template_error", e);
		}finally{
			if(jrs!=null)
				jrs.closeConnection();
		}
		return page;
	}
	
}
