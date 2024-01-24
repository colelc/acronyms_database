package edu.duke.fuqua.db;

import java.sql.Connection;
import java.sql.PreparedStatement;

public interface CreateServiceIF {

	public Integer insert(Connection connection, PreparedStatement ps) throws Exception;

}
