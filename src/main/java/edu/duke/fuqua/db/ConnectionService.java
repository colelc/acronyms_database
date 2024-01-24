package edu.duke.fuqua.db;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.log4j.Logger;

import edu.duke.fuqua.utils.ConfigUtils;

public class ConnectionService implements DbConnectionIF {

	private static Logger log = Logger.getLogger(ConnectionService.class);

	// d, s, or p for ENV
	// oracle or postgres for VENDOR
	public static Connection connect(String VENDOR) {

		try {
			// log.info("Acquiring " + VENDOR + " DB connection for environment=" +
			// ConfigUtils.getENV());

			return new ConnectionService().getConnection(/**/
					ConfigUtils.getProperty(VENDOR + ".driver"), /**/
					ConfigUtils.getProperty(VENDOR + ".url"), /**/
					ConfigUtils.getProperty(VENDOR + ".username"), /**/
					ConfigUtils.getProperty(VENDOR + ".password"));
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			System.exit(99);
		}

		return null;
	}

	@Override
	public Connection getConnection(String DB_DRIVER, String DB_URL, String DB_USERNAME, String DB_PASSWORD) throws Exception {
		Connection connection = null;

		try {
			// Register the JDBC driver
			Class.forName(DB_DRIVER);
			connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
			// log.info("DB Connection successfully acquired");
		} catch (Exception e) {
			throw e;
		}

		return connection;
	}

	@Override
	public void close(Connection connection) throws Exception {
		try {
			if (connection != null) {
				log.info("Closing database connection");
				connection.close();
			}
		} catch (Exception e) {
			throw e;
		}

	}

}
