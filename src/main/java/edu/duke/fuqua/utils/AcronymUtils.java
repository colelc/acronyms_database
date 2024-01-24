package edu.duke.fuqua.utils;

import java.io.File;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.duke.fuqua.db.ConnectionService;
import edu.duke.fuqua.vo.ExcelAcronym;

public class AcronymUtils {

	private static Logger log = Logger.getLogger(AcronymUtils.class);

	public static void loadAcronymData() {
		try {
			String acronymDirectory = ConfigUtils.getProperty("acronym.excel.directory");
			String csvFile = acronymDirectory + File.separator + ConfigUtils.getProperty("acronym.excel.csv.file");
			List<Map<String, String>> csvList = ExcelUtils.csvToList(csvFile);

			populateFuquaAcronyms(csvList);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private static void populateFuquaAcronyms(List<Map<String, String>> csvList) throws Exception {
		try {
			Connection connection = ConnectionService.connect("postgres");
			PostgresUtils service = new PostgresUtils();

			// table = fuqua_acronyms
			String table = ConfigUtils.getProperty("table.name.fuqua.acronyms");
			List<String> columnNames = DdlUtils.getTableColumns(connection, table);

			// template data
			int counter = 1;
			for (Map<String, String> map : csvList) {
				ExcelAcronym ea = new ExcelAcronym(map.get("ACRONYM"), map.get("REFERS TO"), map.get("DEFINITION"), map.get("Area Key"));
				Integer id = service.populateFuquaAcronyms(connection, table, columnNames, ea);
			}

		} catch (

		Exception e) {
			throw e;
		}
	}

}
