package com.jeancoder.core.power;

import java.util.List;
import java.util.Map;

import com.jeancoder.core.exception.JeancoderException;
import com.jeancoder.core.power.result.JeancoderResultSet;
import com.jeancoder.core.power.support.DBBrandNameParser;

public interface DatabasePower {
	/**
	 * 打开一个事务
	 * 使用后需要调用commitTransaction或者rollbackTransaction结束该事务
	 * 否则可能导致大量连接hold造成系统开销或者连接池满
	 */
	public void beginTransaction();
	/**
	 * 提交之前打开的事务
	 */
	public void commitTransaction();
	/**
	 * 回滚之前打开的事务
	 */
	public void rollbackTransaction();
	/**
	 * 执行修改 插入操作
	 * @param sql
	 * @param params
	 * @return 受影响行数
	 * @throws JeancoderException
	 */
	public int doUpdate(String sql,Object... params) throws JeancoderException;
	
	public <T> T doInsert(String sql) throws JeancoderException;
	/**
	 * 执行插入操作
	 * @param sql
	 * @param params
	 * @return 生成的主键值
	 * @throws JeancoderException
	 */
	public Object doInsert(String sql,Object... params)throws JeancoderException;
	/**
	 * 仅支持mysql
	 * 默认使用驼峰转下划线小写 兼容下划线 
	 * 默认不处理null字段
	 * @param dto 自动将dto转换成sql执行
	 * @param primaryKeyFieldName 设置主键列名称 根据该字段是否为null辨别是添加还是修改 如果是添加 将把生成的值填充到该对象
	 * @param processNull 自定义是否操作null字段
	 * @return
	 * @throws JeancoderException
	 */
	public int doUpdateSerialize(Object dto,String primaryKeyFieldName)throws JeancoderException;
	/**
	 * 仅支持mysql
	 * 默认使用驼峰转下划线小写 兼容下划线
	 * @param dto 自动将dto转换成sql执行
	 * @param primaryKeyFieldName 设置主键列名称 根据该字段是否为null辨别是添加还是修改 如果是添加 将把生成的值填充到该对象
	 * @param processNull 自定义是否操作null字段
	 * @return
	 * @throws JeancoderException
	 */
	public int doUpdateSerialize(Object dto,String primaryKeyFieldName,boolean processNull)throws JeancoderException;
	/**
	 * 仅支持mysql
	 * @param dto 自动将dto转换成sql执行
	 * @param primaryKeyFieldName 设置主键列名称 根据该字段是否为null辨别是添加还是修改 如果是添加 将把生成的值填充到该对象
	 * @param parser 自定义表列明映射规则
	 * @param processNull 自定义是否操作null字段
	 * @return
	 * @throws JeancoderException
	 */
	public int doUpdateSerialize(Object dto,String primaryKeyFieldName,DBBrandNameParser parser,boolean processNull)throws JeancoderException;
	@Deprecated
	public JeancoderResultSet doQuery(String sql,Object... params) throws JeancoderException;
	public <T> List<T> doQueryList(Class<?> T,String sql) throws JeancoderException;
	public <T> List<T> doQueryList(Class<?> T,String sql,Object... params) throws JeancoderException;
	public <T> T doQueryUnique(Class<?> T,String sql,Object... params) throws JeancoderException;
	public <T> T doQueryUnique(Class<?> T,String sql) throws JeancoderException;
	public List<Map<String,Object>> doQueryList(String sql,Object... params) throws JeancoderException;
	public Map<String,Object> doQueryUnique(String sql,Object... params) throws JeancoderException;
	public <T> T doQueryUniqueScalar(Class<?> T,String sql,Object... params) throws JeancoderException;
	public <T> T doQueryUniqueScalar(Class<?> T,String sql) throws JeancoderException;
}
