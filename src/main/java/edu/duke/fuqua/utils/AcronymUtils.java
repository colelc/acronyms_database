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
import edu.duke.fuqua.vo.Tag;

public class AcronymUtils {

	private static Logger log = Logger.getLogger(AcronymUtils.class);

	public static void loadAcronymPermissions() throws Exception {
		try {
			Connection connection = ConnectionService.connect("postgres");
			PostgresUtils service = new PostgresUtils();

			String table = ConfigUtils.getProperty("table.name.fuqua.acronym.permissions");
			List<String> columnNames = DdlUtils.getTableColumns(connection, table);

			Integer id = service.populateFuquaAcronymPermissions(connection, table, columnNames, "lcc9");
		} catch (Exception e) {
			throw e;
		}
	}

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
				String splitter = "";
				if (t.contains("\r\n")) {
					splitter = "\r\n";
				} else if (t.contains("\r")) {
					splitter = "\r";
				} else if (t.contains("\n")) {
					splitter = "\n";
				} else {
					splitter = null;
				}

				if (splitter != null) {
					splitterList.clear();
					splitterList.addAll(Arrays.asList(t.split(splitter)));

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

	public static void loadAcronymTagMap() {
		try {
			Connection connection = ConnectionService.connect("postgres");
			PostgresUtils service = new PostgresUtils();

			List<ExcelAcronym> list = service.queryFuquaAcronyms(connection, null);
			if (list == null || list.size() == 0) {
				throw new Exception("No data in fuqua_acronyms table - cannot populate fuqua_acronym_tags table");
			}

			List<Tag> tagList = service.queryFuquaAcronymTags(connection, null);
			if (tagList == null || tagList.size() == 0) {
				throw new Exception("No data in fuqua_acronym_tags table - cannot populate fuqua_acronym_tag_map table");
			}

			String table = ConfigUtils.getProperty("table.name.fuqua.acronym.tag.map");
			List<String> columnNames = DdlUtils.getTableColumns(connection, table);

			for (ExcelAcronym a : list) {
				// if (a.getAcronym().trim().toLowerCase().compareTo("wic") == 0) {
				// continue;
				// }
				Integer tagId = tagList.stream()/**/
						.filter(f -> f.getName().trim().toLowerCase().compareTo(a.getAreaKey().trim().toLowerCase()) == 0)/**/
						.map(m -> m.getId())/**/
						.findFirst().get();

				service.populateFuquaAcronymTagMap(connection, table, columnNames, a.getId(), tagId);
			}

			// now take care of WIC
//			Integer acronymId = list.stream().filter(f -> f.getAcronym().compareTo("WIC") == 0).map(m -> m.getId()).findFirst().get();
//			Integer hsmTagId = tagList.stream().filter(f -> f.getName().compareTo("HSM") == 0).map(m -> m.getId()).findFirst().get();
//			Integer mbaTagId = tagList.stream().filter(f -> f.getName().compareTo("MBA") == 0).map(m -> m.getId()).findFirst().get();
//			Integer mmsTagId = tagList.stream().filter(f -> f.getName().compareTo("MMS") == 0).map(m -> m.getId()).findFirst().get();
//
//			service.populateFuquaAcronymTagMap(connection, table, columnNames, acronymId, hsmTagId);
//			service.populateFuquaAcronymTagMap(connection, table, columnNames, acronymId, mbaTagId);
//			service.populateFuquaAcronymTagMap(connection, table, columnNames, acronymId, mmsTagId);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public static void updateTagString() {
		try {
			// String acronymDirectory = ConfigUtils.getProperty("acronym.excel.directory");
			// String csvFile = acronymDirectory + File.separator + ConfigUtils.getProperty("acronym.excel.csv.file");
			// List<Map<String, String>> csvList = ExcelUtils.csvToList(csvFile);

			// populateFuquaAcronyms(csvList);
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
				ExcelAcronym ea = new ExcelAcronym(map.get("ACRONYM").trim(), /**/
						map.get("REFERS TO").trim(), /**/
						map.get("DEFINITION").trim(), /**/
						map.get("Area Key").trim(), /**/
						map.get("Area Key").toUpperCase().trim()/**/
				);
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
				Integer id = service.populateFuquaAcronymTag(connection, table, columnNames, tag.toUpperCase().trim());
			}

		} catch (Exception e) {
			throw e;
		}
	}

}
