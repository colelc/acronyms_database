package edu.duke.fuqua.utils;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

	public static void loadAcronymTags() {
		try {
			Connection connection = ConnectionService.connect("postgres");
			PostgresUtils service = new PostgresUtils();
			List<ExcelAcronym> list = service.queryFuquaAcronyms(connection, null);
			if (list == null || list.size() == 0) {
				throw new Exception("No data in fuqua_acronyms table - cannot populate fuqua_acronym_tags table");
			}

			List<String> tagList = list.stream().map(m -> m.getAreaKey()).distinct().collect(Collectors.toList());

			// hack for multiple tags in same Excel cell
			List<String> copy = new ArrayList<>();
			List<String> splitterList = new ArrayList<>();

			for (int i = 0; i < tagList.size(); i++) {
				String t = tagList.get(i);
				if (t.contains("\r") || t.contains("\r\n") || t.contains("\n")) {
					splitterList.clear();
					splitterList.addAll(Arrays.asList(t.split("\n")));

					for (String s : splitterList) {
						copy.add(s);
						log.info("Added: " + s);
					}
				} else {
					copy.add(t);
					log.info("Added: " + t);
				}
			}

			populateFuquaAcronymTags(copy);
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
			for (Map<String, String> map : csvList) {
				ExcelAcronym ea = new ExcelAcronym(map.get("ACRONYM"), map.get("REFERS TO"), map.get("DEFINITION"), map.get("Area Key"));
				Integer id = service.populateFuquaAcronyms(connection, table, columnNames, ea);
			}

		} catch (

		Exception e) {
			throw e;
		}
	}

	private static void populateFuquaAcronymTags(List<String> tagList) throws Exception {
		try {
			Connection connection = ConnectionService.connect("postgres");
			PostgresUtils service = new PostgresUtils();

			// table = fuqua_acronyms
			String table = ConfigUtils.getProperty("table.name.fuqua.acronym.tags");
			List<String> columnNames = DdlUtils.getTableColumns(connection, table);

			// tags
			for (String tag : tagList) {
				log.info(tag);
				Integer id = service.populateFuquaAcronymTag(connection, table, columnNames, tag);
			}

		} catch (Exception e) {
			throw e;
		}
	}

}
