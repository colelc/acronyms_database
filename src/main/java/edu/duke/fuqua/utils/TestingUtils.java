package edu.duke.fuqua.utils;

import org.apache.log4j.Logger;

public class TestingUtils {

	private static Logger log = Logger.getLogger(TestingUtils.class);

	public static void main(String[] args) {
		try {
			DdlUtils.deleteRedefinePostgresqlTables();

			// new case
			AcronymUtils.loadAcronymData();

			AcronymUtils.loadAcronymTags();

			// QueryUtils.queryTests();

			log.info("DONE");
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

}
