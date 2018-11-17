package com.jeancoder.jdbc.template;

import java.util.List;

import com.jeancoder.jdbc.JcPage;

/**
 * Use by inherit this class
 * @author jackielee
 *
 * @param <T>
 */
public class CarryJcDaoTemplate<T> extends CommonJcDaoTemplate<T> {

	//protected Class<T> entityClass;
	
	public CarryJcDaoTemplate() {
		//this.entityClass = this.getSuperClassGenricType(getClass());
	}
	
	public T get(String sql, Object...params) {
		Class<T> entityClass = this.getSuperClassGenricType(getClass());
		return super.get(entityClass, sql, params);
	}
	
	public JcPage<T> find(JcPage<T> page, String sql, final Object... params) {
		Class<T> entityClass = this.getSuperClassGenricType(getClass());
		return this.find(entityClass, page, sql, params);
	}

	public List<T> find(String sql, final Object... params) {
		Class<T> entityClass = this.getSuperClassGenricType(getClass());
		return this.find(entityClass, sql, params);
	}
}
