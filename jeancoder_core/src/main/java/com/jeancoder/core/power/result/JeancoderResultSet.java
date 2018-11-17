package com.jeancoder.core.power.result;

import java.sql.ResultSet;

public interface JeancoderResultSet {
	public ResultSet getResultSet();
	public void closeConnection();
}
