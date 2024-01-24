package edu.duke.fuqua.db;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;

public class UpdateService implements UpdateServiceIF {

	private static Logger log = Logger.getLogger(UpdateService.class);

	@Override
	public void update(Connection connection, PreparedStatement ps) throws Exception {
		try {
			ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		}
	}

}
