package com.jeancoder.jdbc;

import java.util.List;

import com.jeancoder.app.sdk.source.DatabaseSource;
import com.jeancoder.core.power.DatabasePower;
import com.jeancoder.jdbc.template.CommonJcDaoTemplate;

public class JcTemplate {
	
	final static JcTemplate INSTANCE = new JcTemplate();
	
	private JcTemplate() {}
	
	public static JcTemplate INSTANCE() {
		return INSTANCE;
	}
	
	public void startTrans() {
		DatabasePower dp = DatabaseSource.getDatabasePower();
		dp.beginTransaction();
	}
	
	public void commitTrans() {
		DatabasePower dp = DatabaseSource.getDatabasePower();
		dp.commitTransaction();
	}
	
	public void rbTrans() {
		DatabasePower dp = DatabaseSource.getDatabasePower();
		dp.rollbackTransaction();
	}
	
	public <T> List<?> findRawData(Class<T> mapclass, String sql, Object...params) {
		CommonJcDaoTemplate<T> temp = new CommonJcDaoTemplate<T>();
		return temp.findRawData(mapclass, sql, params);
	}
	
	public <T> JcPage<?> findRawData(Class<T> mapclass, JcPage<Object[]> page, String sql, Object...params) {
		CommonJcDaoTemplate<T> temp = new CommonJcDaoTemplate<T>();
		return temp.findRawData(mapclass, page, sql, params);
	}

	public <T> JcPage<T> find(Class<T> mapclass, JcPage<T> page, String sql, Object...params) {
		CommonJcDaoTemplate<T> temp = new CommonJcDaoTemplate<T>();
		return temp.find(mapclass, page, sql, params);
	}
	
	public <T> List<T> find(Class<T> mapclass, String sql, Object...params) {
		CommonJcDaoTemplate<T> temp = new CommonJcDaoTemplate<T>();
		return temp.find(mapclass, sql, params);
	}
	
	public <T> int batchExecute(String sql, Object...params) {
		CommonJcDaoTemplate<T> temp = new CommonJcDaoTemplate<T>();
		return temp.batchExecute(sql, params);
	}
	
	public <T, X> X save(T entity) {
		CommonJcDaoTemplate<T> temp = new CommonJcDaoTemplate<T>();
		X ret = temp.save(entity);
		return ret;
	}
	
	public <T> int update(T entity) {
		CommonJcDaoTemplate<T> temp = new CommonJcDaoTemplate<T>();
		return temp.update(entity);
	}
	
	public <T> T get(Class<T> mapclass, String sql, Object...params) {
		CommonJcDaoTemplate<T> temp = new CommonJcDaoTemplate<T>();
		return temp.get(mapclass, sql, params);
	}
}
