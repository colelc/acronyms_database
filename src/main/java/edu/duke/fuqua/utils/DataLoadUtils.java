package edu.duke.fuqua.utils;

import org.apache.log4j.Logger;

public class DataLoadUtils {

	private static Logger log = Logger.getLogger(DataLoadUtils.class);

	public static void main(String[] args) {
		try {
			boolean loadAcronyms = ConfigUtils.getPropertyAsBoolean("load.acronyms");
			boolean loadDar = ConfigUtils.getPropertyAsBoolean("load.dar");

			if (loadAcronyms) {
				log.info("Loading ACRONYMS data");
				;
				DdlUtils.deleteRedefinePostgresqlTables();
				AcronymUtils.loadAcronymData();
				AcronymUtils.loadAcronymTags();
				AcronymUtils.loadAcronymTagMap();
				QueryUtils.queryTests();
				log.info("ACRONYMS work is complete");
			}

			if (loadDar) {
				log.info("Loading DAR data");
				;
				DdlUtils.deleteRedefineDARPostgresqlTables();
				DARUtils.loadDARBoardMembers();
//				AcronymUtils.loadAcronymData();
//				AcronymUtils.loadAcronymTags();
//				AcronymUtils.loadAcronymTagMap();
//				QueryUtils.queryTests();
				log.info("DAR work is complete");
			}
			log.info("DONE");
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

}
