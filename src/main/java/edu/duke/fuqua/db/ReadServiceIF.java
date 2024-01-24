package edu.duke.fuqua.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public interface ReadServiceIF {

	public ResultSet doQuery(Connection connection, String query) throws Exception;

	public ResultSet doPreparedQuery(Connection connection, PreparedStatement ps) throws Exception;

	public int queryForRowCount(Connection connection, String table) throws Exception;

	public List<String> getColumnsInTable(Connection connection, String tableName) throws Exception;
}
