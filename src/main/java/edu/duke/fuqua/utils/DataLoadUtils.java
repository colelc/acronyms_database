package edu.duke.fuqua.utils;

import org.apache.log4j.Logger;

public class DataLoadUtils {

	private static Logger log = Logger.getLogger(DataLoadUtils.class);

	public static void main(String[] args) {
		try {
			DdlUtils.deleteRedefinePostgresqlTables();

			// new case
			AcronymUtils.loadAcronymData();

			AcronymUtils.loadAcronymTags();

			AcronymUtils.loadAcronymTagMap();

			// QueryUtils.queryTests();

			log.info("DONE");
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

}
