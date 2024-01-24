package edu.duke.fuqua.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

public class CreateService implements CreateServiceIF {

	private static Logger log = Logger.getLogger(CreateService.class);

	@Override
	public Integer insert(Connection connection, PreparedStatement ps) throws Exception {
		try {
			ResultSet rs = ps.executeQuery();
			rs.next();
			Integer id = rs.getInt(1);
			return id;
		} catch (Exception e) {
			throw e;
		}

	}

}
