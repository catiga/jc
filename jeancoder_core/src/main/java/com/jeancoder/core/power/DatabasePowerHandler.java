package com.jeancoder.core.power;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.alibaba.druid.pool.DruidDataSource;
import com.jeancoder.core.exception.JeancoderException;
import com.jeancoder.core.power.exception.DbPowerDoQueryFailedException;
import com.jeancoder.core.power.exception.DbPowerDoUpdateFailedException;
import com.jeancoder.core.power.exception.PowerConnectFailedException;
import com.jeancoder.core.power.exception.PowerDriveClassLoadFailedException;
import com.jeancoder.core.power.exception.TransactionCloseFailed;
import com.jeancoder.core.power.exception.TransactionCommitFailed;
import com.jeancoder.core.power.exception.TransactionConnectFailed;
import com.jeancoder.core.power.exception.TransactionHasBeginException;
import com.jeancoder.core.power.exception.TransactionNotBeginException;
import com.jeancoder.core.power.result.JeancoderResultSet;
import com.jeancoder.core.power.result.JeancoderResultSetImpl;
import com.jeancoder.core.power.support.DBBrandNameParser;
import com.jeancoder.core.power.support.DBOperateSqlBody;

/**
 * 关系型数据库操作器
 * @author wow zhang_gh@cpis.cn
 * @date 2018年6月8日
 */
public class DatabasePowerHandler extends PowerHandler implements DatabasePower{
	/**
	 * 当开发者手动开启事务后 会在此线程连接中放入一个dataSource变量 用于管理事务
	 */
	private static final ThreadLocal<Connection> transactionDataSource = new ThreadLocal<Connection>();
	
	private DataSource dataSource;

	@Override
	public void init(PowerConfig config)throws JeancoderException{
		DatabasePowerConfig dbconfig = (DatabasePowerConfig)config;
		try {
			Class.forName(dbconfig.getDriveClass());
		} catch (ClassNotFoundException e) {
			throw new PowerDriveClassLoadFailedException(dbconfig.getDriveClass());
		}
		try {
			//阿里巴巴开源的连接池
			DruidDataSource dds = new DruidDataSource();
			dds.setUrl(dbconfig.getUrl());
			dds.setUsername(dbconfig.getUser());
			dds.setPassword(dbconfig.getPassword());
			//<!-- 配置初始化大小、最小、最大 -->  
			dds.setInitialSize(10);
			dds.setMinIdle(5);
			dds.setMaxActive(30);
			//<!-- 配置获取连接等待超时的时间 -->  
			dds.setMaxWait(60000);
			//<!-- 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒 -->  
			dds.setTimeBetweenEvictionRunsMillis(60000);
			//<!-- 配置一个连接在池中最小生存的时间，单位是毫秒 -->  
			dds.setMinEvictableIdleTimeMillis(300000);
			dds.setValidationQuery("SELECT 'x'");
			dds.setTestWhileIdle(true);
			dds.setTestOnBorrow(false);
			dds.setTestOnReturn(false);
			//<!-- 打开PSCache，并且指定每个连接上PSCache的大小 -->  
			dds.setPoolPreparedStatements(true);
			dds.setMaxPoolPreparedStatementPerConnectionSize(20);
			//<!-- 配置监控统计拦截的filters，去掉后监控界面sql无法统计 -->  
			dds.setFilters("stat");
			dataSource = dds;
		} catch (SQLException e) {
			throw new PowerConnectFailedException();
		}
	}
	
	/**
	 * 打开一个事务
	 * 使用后需要调用commitTransaction或者rollbackTransaction结束该事务
	 * 否则可能导致大量连接hold造成系统开销
	 */
	public void beginTransaction(){
		if(DatabasePowerHandler.transactionDataSource.get() != null) {
			throw new TransactionHasBeginException();
		}
		try {
			Connection conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			DatabasePowerHandler.transactionDataSource.set(conn);
		}catch(Exception e) {
			throw new TransactionConnectFailed(e);
		}
	}
	
	/**
	 * 提交之前打开的事务
	 */
	public void commitTransaction(){
		if(DatabasePowerHandler.transactionDataSource.get() == null) {
			throw new TransactionNotBeginException();
		}
		try {
			DatabasePowerHandler.transactionDataSource.get().commit();
		}catch(Exception e) {
			throw new TransactionCommitFailed(e);
		}
		
		try {
			DatabasePowerHandler.transactionDataSource.get().close();
			DatabasePowerHandler.transactionDataSource.set(null);
		}catch(Exception e) {
			throw new TransactionCommitFailed(e);
		}
	}
	
	/**
	 * 回滚之前打开的事务
	 */
	public void rollbackTransaction(){
		if(DatabasePowerHandler.transactionDataSource.get() == null) {
			throw new TransactionNotBeginException();
		}
		try {
			DatabasePowerHandler.transactionDataSource.get().rollback();
		}catch(Exception e) {
			throw new TransactionCommitFailed(e);
		}
		
		try {
			DatabasePowerHandler.transactionDataSource.get().close();
			DatabasePowerHandler.transactionDataSource.set(null);
		}catch(Exception e) {
			throw new TransactionCommitFailed(e);
		}
	}
	
	private Connection getConn(){
		if(DatabasePowerHandler.transactionDataSource.get() == null) {
			try {
				return dataSource.getConnection();
			}catch(SQLException e) {
				throw new TransactionConnectFailed(e);
			}
		}else {
			return DatabasePowerHandler.transactionDataSource.get();
		}
	}
	
	private void closeConn(Connection conn){
		if(conn == null) {
			return;
		}
		if(DatabasePowerHandler.transactionDataSource.get() == null) {
			//说明没打开过事务 用完就关闭
			try {
				conn.close();
			}catch(SQLException e) {
				throw new TransactionCloseFailed(e);
			}
		}else {
			//说明打开过事务 不需要做什么
		}
	}
	
	/**
	 * 执行修改 插入操作
	 * @param sql
	 * @param params
	 * @return 受影响行数
	 * @throws JeancoderException
	 */
	public int doUpdate(String sql,Object... params) throws JeancoderException{
		Connection conn = getConn();
		try {
			//System.out.println(sql);
			QueryRunner run = new QueryRunner(dataSource);
			int res = run.update(conn,sql,params);
			closeConn(conn);
			return res;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DbPowerDoUpdateFailedException(e);
		}finally {
			closeConn(conn);
		}
	}
	
	public <T> T doInsert(String sql) throws JeancoderException {
		Connection conn = getConn();
		try {
			ResultSetHandler<List<T>> hand = new ColumnListHandler<T>();
			QueryRunner run = new QueryRunner(dataSource);
			List<T> ret = run.insert(conn,sql, hand);
			closeConn(conn);
			if(ret!=null&&!ret.isEmpty()) {
				return ret.get(0);
			}
			return null;
		} catch (SQLException e) {
			throw new DbPowerDoUpdateFailedException(e);
		} finally {
			closeConn(conn);
		}
	}
	
	/**
	 * 执行插入操作
	 * @param sql
	 * @param params
	 * @return 生成的主键值
	 * @throws JeancoderException
	 */
	public Object doInsert(String sql,Object... params)throws JeancoderException{
		Connection conn = getConn();
		try {
			ResultSetHandler<Object> h = new ResultSetHandler<Object>() {
			    public Object handle(ResultSet rs) throws SQLException {
			    		rs.next();
			    		return rs.getObject(1);
			    }
			};
			QueryRunner run = new QueryRunner(dataSource);
			Object res = run.insert(conn,sql, h, params);
			return res;
		} catch (SQLException e) {
			throw new DbPowerDoUpdateFailedException(e);
		} finally {
			closeConn(conn);
		}
	}
	
	/**
	 * 默认使用驼峰转下划线小写 兼容下划线
	 * 默认不处理null字段
	 * @param dto 自动将dto转换成sql执行
	 * @param primaryKeyFieldName 设置主键列名称 根据该字段是否为null辨别是添加还是修改 如果是添加 将把生成的值填充到该对象
	 * @return
	 * @throws JeancoderException
	 */
	public int doUpdateSerialize(Object dto,String primaryKeyFieldName)throws JeancoderException{
		return doUpdateSerialize(dto,primaryKeyFieldName,false);
	}
	
	/**
	 * 默认使用驼峰转下划线小写 兼容下划线
	 * @param dto 自动将dto转换成sql执行
	 * @param primaryKeyFieldName 设置主键列名称 根据该字段是否为null辨别是添加还是修改 如果是添加 将把生成的值填充到该对象
	 * @param processNull 自定义是否操作null字段
	 * @return
	 * @throws JeancoderException
	 */
	public int doUpdateSerialize(Object dto,String primaryKeyFieldName,boolean processNull)throws JeancoderException{
		return doUpdateSerialize(dto,primaryKeyFieldName,DBBrandNameParser.HUMP_UNDERLINE_LOWERCASE,processNull);
	}
	
	/**
	 * @param dto 自动将dto转换成sql执行
	 * @param primaryKeyFieldName 设置主键列名称 根据该字段是否为null辨别是添加还是修改 如果是添加 将把生成的值填充到该对象
	 * @param parser 自定义表列明映射规则
	 * @param processNull 自定义是否操作null字段
	 * @return
	 * @throws JeancoderException
	 */
	public int doUpdateSerialize(Object dto,String primaryKeyFieldName,DBBrandNameParser parser,boolean processNull)throws JeancoderException{
		if(primaryKeyFieldName == null) {
			throw new DbPowerDoUpdateFailedException("Param primaryKeyFieldName can not be null.");
		}
		
		Class<?> primaryKeyFieldType = null;
		Method primaryKeyFieldSetMethod = null;
		Map<String,String> fieldMethodMap = new HashMap<String,String>();
		for(Field field : dto.getClass().getDeclaredFields()) {
			//排除一些非用户定义的字段
			if("metaClass".equals(field.getName())) {
				continue;
			}
			String fname = field.getName();
			String getMethod = "get"+Character.toUpperCase(fname.charAt(0))+fname.substring(1);
			try {
				dto.getClass().getDeclaredMethod(getMethod);
			} catch (NoSuchMethodException | SecurityException e) {
				//不是需要处理的字段
				continue;
			}
			if(field.getName().equals(primaryKeyFieldName)) {
				primaryKeyFieldType = field.getType();
				String setMethod = "set"+Character.toUpperCase(fname.charAt(0))+fname.substring(1);
				try {
					primaryKeyFieldSetMethod = dto.getClass().getDeclaredMethod(setMethod,primaryKeyFieldType);
				} catch (NoSuchMethodException | SecurityException e) {
					throw new DbPowerDoUpdateFailedException(e);
				}
			}
			fieldMethodMap.put(fname, getMethod);
		}
		
		if(fieldMethodMap.get(primaryKeyFieldName) == null) {
			throw new DbPowerDoUpdateFailedException("Param primaryKeyFieldName '"+primaryKeyFieldName+"' not field with "+dto.getClass().getName()+".");
		}
		
		Object primaryKeyFieldValue = null;
		try {
			primaryKeyFieldValue = dto.getClass().getMethod(fieldMethodMap.get(primaryKeyFieldName)).invoke(dto);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new DbPowerDoUpdateFailedException(e);
		}
		
		StringBuffer mainSql = new StringBuffer();
		
		if(primaryKeyFieldValue == null) {
			DBOperateSqlBody sqlBody = geneInsertSqlBody(fieldMethodMap,primaryKeyFieldName,parser,dto,processNull);
			mainSql.append("INSERT INTO "+parser.parse(dto.getClass().getSimpleName()));
			mainSql.append(sqlBody.getBody());
			
			Object geneKeyValue = doInsert(mainSql.toString(),sqlBody.getParamse());
			try {
				primaryKeyFieldSetMethod.invoke(dto, geneKeyValue);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				//TODO 设置生成的值失败了 这里应该回滚还是该怎么样
				throw new DbPowerDoUpdateFailedException(e);
			}
			
			//插入成功
			return 1;
		}else {
			DBOperateSqlBody sqlBody = geneUpdateSqlBody(fieldMethodMap,primaryKeyFieldName,parser,dto,processNull);
			mainSql.append("UPDATE "+parser.parse(dto.getClass().getSimpleName()));
			mainSql.append(sqlBody.getBody());
			
			//修改成功
			return doUpdate(mainSql.toString(),sqlBody.getParamse());
		}
	}
	
	private DBOperateSqlBody geneInsertSqlBody(Map<String,String> fieldMethodMap,String primaryKeyFieldName,DBBrandNameParser parser,Object dto,boolean processNull)throws JeancoderException {
		StringBuffer namers = new StringBuffer("(");
		StringBuffer valuyes = new StringBuffer("(");
		List<Object> params = new ArrayList<Object>();
		
		for(String fieldName : fieldMethodMap.keySet()) {
			Object param = null;
			try {
				param = dto.getClass().getDeclaredMethod(fieldMethodMap.get(fieldName)).invoke(dto);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				throw new DbPowerDoUpdateFailedException("Can not process field "+fieldName+".");
			}
			if(!processNull) {
				if(param == null) {
					continue;
				}
			}
			if(fieldName.equals(primaryKeyFieldName)) {
				continue;
			}
			if(namers.length() != 1) {
				namers.append(",");
			}
			namers.append(parser.parse(fieldName));
			if(valuyes.length() != 1) {
				valuyes.append(",");
			}
			valuyes.append("?");
			
			params.add(param);
		}
		namers.append(")");
		valuyes.append(")");
		
		DBOperateSqlBody sqlBody = new DBOperateSqlBody();
		
		sqlBody.setBody(namers.toString()+" VALUES "+ valuyes.toString());
		sqlBody.setParamse(params.toArray(new Object[params.size()]));
		
		return sqlBody;
	}
	
	private DBOperateSqlBody geneUpdateSqlBody(Map<String,String> fieldMethodMap,String primaryKeyFieldName,DBBrandNameParser parser,Object dto,boolean processNull)throws JeancoderException {
		StringBuffer updater = new StringBuffer(" SET");
		
		List<Object> params = new ArrayList<Object>();
		
		for(String fieldName : fieldMethodMap.keySet()) {
			Object param = null;
			try {
				param = dto.getClass().getDeclaredMethod(fieldMethodMap.get(fieldName)).invoke(dto);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				throw new DbPowerDoUpdateFailedException("Can not process field "+fieldName+".");
			}
			if(!processNull) {
				if(param == null) {
					continue;
				}
			}
			if(fieldName.equals(primaryKeyFieldName)) {
				continue;
			}
			if(updater.length() != 4) {
				updater.append(",");
			}
			updater.append(" "+parser.parse(fieldName)+" = ?");
			
			params.add(param);
		}
		
		updater.append(" WHERE "+parser.parse(primaryKeyFieldName) +" = ?");
		
		Object primaryValue = null;
		try {
			primaryValue = dto.getClass().getDeclaredMethod(fieldMethodMap.get(primaryKeyFieldName)).invoke(dto);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new DbPowerDoUpdateFailedException("Can not process field "+primaryKeyFieldName+".");
		}
		params.add(primaryValue);
		
		DBOperateSqlBody sqlBody = new DBOperateSqlBody();
		
		sqlBody.setBody(updater.toString());
		sqlBody.setParamse(params.toArray(new Object[params.size()]));
		
		return sqlBody;
	}
	
	public JeancoderResultSet doQuery(String sql,Object... params) throws JeancoderException{
		PreparedStatement statement = null;
		Connection connection = null;
		try {
			println(sql, params);
			QueryRunner run = new QueryRunner();
			connection = dataSource.getConnection();
			connection.setReadOnly(true);
			
			statement = connection.prepareStatement(sql);
			
			//借助apache common-dbutil 填充参数
			run.fillStatement(statement, params);
			
			ResultSet res = statement.executeQuery();
			
			JeancoderResultSetImpl resultSet = new JeancoderResultSetImpl();
			resultSet.addStatement(statement);
			resultSet.setConnection(connection);
			resultSet.setResultSet(res);
			return resultSet;
		} catch (SQLException e) {
			if(statement!=null) {
				try {
					statement.close();
					this.closeConn(connection);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			throw new DbPowerDoQueryFailedException(e);
		}
	}
	
	public <T> List<T> doQueryList(Class<?> T,String sql) throws JeancoderException{
		Connection conn = getConn();
		try {
			//System.out.println(sql);
			QueryRunner run = new QueryRunner(dataSource);
			@SuppressWarnings("unchecked")
			ResultSetHandler<List<T>> h = new BeanListHandler<T>((Class<? extends T>) T);
			List<T> rows = run.query(conn,sql, h);
			closeConn(conn);
			return rows;
		} catch (SQLException e) {
			throw new DbPowerDoQueryFailedException(e);
		}finally {
			closeConn(conn);
		}
	}
	public <T> List<T> doQueryList(Class<?> T,String sql,Object... params) throws JeancoderException{
		Connection conn = getConn();
		try {
			QueryRunner run = new QueryRunner(dataSource);
			@SuppressWarnings("unchecked")
			ResultSetHandler<List<T>> h = new BeanListHandler<T>((Class<? extends T>) T);
			List<T> rows = run.query(conn,sql, h,params);
			closeConn(conn);
			return rows;
		} catch (SQLException e) {
			throw new DbPowerDoQueryFailedException(e);
		}finally {
			closeConn(conn);
		}
	}
	public List<Map<String,Object>> doQueryList(String sql,Object... params) throws JeancoderException{
		Connection conn = getConn();
		try {
			ResultSetHandler<List<Map<String,Object>>> h = new ResultSetHandler<List<Map<String,Object>>>() {
			    public List<Map<String,Object>> handle(ResultSet rs) throws SQLException {
			    		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
			    		while(rs.next()) {
			    			Map<String,Object> map = new HashMap<String,Object>();
						ResultSetMetaData meta = rs.getMetaData();
						int cols = meta.getColumnCount();
	
						for (int i = 0; i < cols; i++) {
							map.put(meta.getColumnLabel(i+1) != null ? meta.getColumnLabel(i+1) : meta.getColumnName(i+1), rs.getObject(i + 1));
						}
						resultList.add(map);
			    		}
			    		return resultList;
			    }
			};
			QueryRunner run = new QueryRunner(dataSource);
			List<Map<String,Object>> result = run.query(conn,sql, h,params);
			closeConn(conn);
			return result;
		} catch (SQLException e) {
			throw new DbPowerDoQueryFailedException(e);
		}finally {
			closeConn(conn);
		}
	}
	
	public Map<String,Object> doQueryUnique(String sql,Object... params) throws JeancoderException{
		List<Map<String,Object>> rows = doQueryList(sql,params);
		if(rows == null || rows.isEmpty()) {
			return null;
		}else if(rows.size() != 1) {
			throw new DbPowerDoQueryFailedException("Not unique result.");
		}
		return rows.get(0);
	}
	
	public <T> T doQueryUnique(Class<?> T,String sql,Object... params) throws JeancoderException{
		List<T> rows = doQueryList(T, sql, params);
		if(rows == null || rows.isEmpty()) {
			return null;
		}else if(rows.size() != 1) {
			throw new DbPowerDoQueryFailedException("Not unique result.");
		}
		return rows.get(0);
	}
	
	public <T> T doQueryUnique(Class<?> T,String sql) throws JeancoderException{
		List<T> rows = doQueryList(T, sql);
		if(rows == null || rows.isEmpty()) {
			return null;
		}else if(rows.size() != 1) {
			throw new DbPowerDoQueryFailedException("Not unique result.");
		}
		return rows.get(0);
	}
	
	public <T> T doQueryUniqueScalar(Class<?> T,String sql,Object... params) throws JeancoderException{
		Connection conn = getConn();
		try {
			QueryRunner run = new QueryRunner(dataSource);
			ResultSetHandler<T> h = new ScalarHandler<T>();
			T res = run.query(conn,sql, h,params);
			closeConn(conn);
			return res;
		} catch (SQLException e) {
			throw new DbPowerDoQueryFailedException(e);
		}finally {
			closeConn(conn);
		}
	}
	
	public <T> T doQueryUniqueScalar(Class<?> T,String sql) throws JeancoderException{
		Connection conn = getConn();
		try {
			QueryRunner run = new QueryRunner(dataSource);
			ResultSetHandler<T> h = new ScalarHandler<T>();
			T res = run.query(conn,sql, h);
			closeConn(conn);
			return res;
		} catch (SQLException e) {
			throw new DbPowerDoQueryFailedException(e);
		}finally {
			closeConn(conn);
		}
	}
	

	private void println(String sql, final Object... params) {
//		System.out.print("sql :" + sql);
//		if (params == null) {
//			return;
//		}
//		for ( Object obj : params) {
//			if (obj == null) {
//				continue;
//			} 
//			System.out.print(" " + obj.toString());
//		}
//		System.out.println();
	}
}
