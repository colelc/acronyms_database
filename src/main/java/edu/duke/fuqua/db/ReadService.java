package edu.duke.fuqua.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.duke.fuqua.utils.ConfigUtils;

public class ReadService implements ReadServiceIF {

	private static String schemaName;
	private static Logger log = Logger.getLogger(ReadService.class);

	@Override
	public ResultSet doQuery(Connection connection, String query) throws Exception {
		try {
			Statement stmt = connection.createStatement();
			return stmt.executeQuery(query);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public ResultSet doPreparedQuery(Connection connection, PreparedStatement ps) throws Exception {
		try {
			return ps.executeQuery();
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public int queryForRowCount(Connection connection, String table) throws Exception {
		try {
			ResultSet rs = doQuery(connection, "SELECT count(*) FROM " + table);
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (Exception e) {
			throw e;
		}
		return 0;
	}

	@Override
	public List<String> getColumnsInTable(Connection connection, String tableName) throws Exception {
		try {
			List<String> columns = new ArrayList<>();

			String sql = "select column_name from information_schema.columns " /**/
					+ " where table_schema = \'" + getSchemaName() + "\'" /**/
					+ " and table_name = \'" + tableName + "\' "/**/
					+ " order by ordinal_position ";

			// log.info(sql);

			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				columns.add(rs.getString("column_name"));
			}

			return columns;
		} catch (Exception e) {
			throw e;
		}

	}

	public String getSchemaName() throws Exception {
		try {
			if (schemaName == null) {
				schemaName = ConfigUtils.getProperty("db.name");
			}
		} catch (Exception e) {
			throw e;
		}

		return schemaName;
	}

}
