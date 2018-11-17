package com.jeancoder.core.power.result;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JeancoderResultSetImpl implements JeancoderResultSet{
	private Connection connection;
	private ResultSet resultSet;
	private List<Statement> statements = new ArrayList<Statement>();
	@Override
	public ResultSet getResultSet() {
		return resultSet;
	}
	@Override
	public void closeConnection() {
		try {
			for(Statement sta : statements) {
				sta.close();
			}
		}catch(Exception e) {
			//关闭失败 无需处理
		}
		try {
			resultSet.close();
		}catch (SQLException e) {
			//关闭失败 无需处理
		}
		try {
			connection.close();
		} catch (SQLException e) {
			//关闭失败 无需处理
		}
	}
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	public void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}
	public void addStatement(Statement sta) {
		statements.add(sta);
	}
}
