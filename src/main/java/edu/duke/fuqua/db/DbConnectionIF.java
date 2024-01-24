package edu.duke.fuqua.db;

import java.sql.Connection;

import org.apache.log4j.Logger;

public interface DbConnectionIF {

	static Logger log = Logger.getLogger(DbConnectionIF.class);

	public Connection getConnection(String driver, String url, String username, String password) throws Exception;

	public void close(Connection c) throws Exception;

}
