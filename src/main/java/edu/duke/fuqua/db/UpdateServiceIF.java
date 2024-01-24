package edu.duke.fuqua.db;

import java.sql.Connection;
import java.sql.PreparedStatement;

public interface UpdateServiceIF {
	public void update(Connection connection, PreparedStatement ps) throws Exception;
}
