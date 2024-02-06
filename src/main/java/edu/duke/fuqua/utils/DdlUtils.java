package edu.duke.fuqua.utils;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import edu.duke.fuqua.db.ConnectionService;
import edu.duke.fuqua.db.ReadService;

public class DdlUtils {

	private static Logger log = Logger.getLogger(DdlUtils.class);

	public static void deleteRedefineDARPostgresqlTables() throws Exception {
		try {
			Connection connection = ConnectionService.connect("postgres");

			DdlUtils s = new DdlUtils();

			s.dropTable(connection, "dar_available_appts");
			// s.dropTable(connection, "dar_board_members");

			// s.createTable(connection, "create_dar_board_members", "dar_board_members");
			s.createTable(connection, "create_dar_available_appts", "dar_available_appts");

		} catch (Exception e) {
			throw e;
		}
	}

	public static void deleteRedefinePostgresqlTables() throws Exception {
		try {
			Connection connection = ConnectionService.connect("postgres");

			DdlUtils s = new DdlUtils();

			s.dropTable(connection, "fuqua_acronym_tag_map");
			s.dropTable(connection, "fuqua_acronym_tags");
			s.dropTable(connection, "fuqua_acronyms");
			s.dropTable(connection, "fuqua_acronym_permissions");

			s.createTable(connection, "create_fuqua_acronym_permissions", "fuqua_acronym_permissions");
			s.createTable(connection, "create_fuqua_acronyms", "fuqua_acronyms");
			s.createTable(connection, "create_fuqua_acronym_tags", "fuqua_acronym_tags");
			s.createTable(connection, "create_fuqua_acronym_tag_map", "fuqua_acronym_tag_map");
		} catch (Exception e) {
			throw e;
		}
	}

	public void createTable(Connection connection, String fileName, String tableName) throws Exception {
		try {
			tableName = dbName(tableName);
			fileName = fileNameExt(fileName);

			// log.info("Creating table " + tableName + " from ddl file " + fileName);

			String sql = FileUtils.readFileAsString(ConfigUtils.getProperty("postgres.ddl.directory") + File.separator + fileName);
			log.info(sql);

			Statement statement = connection.createStatement();
			statement.execute(sql);

			// log.info("Table " + tableName + " has been created");
		} catch (Exception e) {
			throw e;
		}
	}

	public void dropTable(Connection connection, String tableName) throws Exception {
		try {

			String sql = "DROP TABLE IF EXISTS " + dbName(tableName);
			log.info(sql);

			Statement statement = connection.createStatement();
			statement.execute(sql);

			// log.info("Table " + dbName(tableName) + " has been dropped");
		} catch (Exception e) {
			throw e;
		}
	}

	public static String dbName(String tableName) throws Exception {
		try {
			if (!tableName.startsWith(PostgresUtils.getDbName())) {
				tableName = PostgresUtils.getDbName() + "." + tableName;
				// log.info("Appended database name to table name: " + tableName);
			}
		} catch (Exception e) {
			throw e;
		}
		return tableName;
	}

	public static List<String> getTableColumns(Connection connection, String tableName) throws Exception {
		try {
			List<String> columnNamesList = new ReadService().getColumnsInTable(connection, tableName);
			columnNamesList = columnNamesList.stream()/**/
					.filter(f -> f.compareTo("id") != 0 /**/
							&& f.compareTo("created") != 0 /**/
							&& f.compareTo("deleted") != 0 /**/
							&& f.compareTo("deleted_by") != 0 /**/
							&& f.compareTo("last_updated") != 0 /**/
							&& f.compareTo("last_updated_by") != 0)
					.collect(Collectors.toList());
			return columnNamesList;
		} catch (Exception e) {
			throw e;
		}

	}

	public static List<String> getTableColumnsDARAvailableAppts(Connection connection, String tableName) throws Exception {
		try {
			List<String> columnNamesList = new ReadService().getColumnsInTable(connection, tableName);
			columnNamesList = columnNamesList.stream()/**/
					.filter(f -> f.compareTo("id") != 0 /**/
							// && f.compareTo("created") != 0 /**/
							&& f.compareTo("deleted") != 0 /**/
							&& f.compareTo("deleted_by") != 0 /**/
							&& f.compareTo("last_updated") != 0 /**/
							&& f.compareTo("last_updated_by") != 0)
					.collect(Collectors.toList());
			return columnNamesList;
		} catch (Exception e) {
			throw e;
		}

	}

	private static String fileNameExt(String fileName) throws Exception {
		try {
			if (!fileName.endsWith(".sql")) {
				fileName = fileName + ".sql";
			}
		} catch (Exception e) {
			throw e;
		}
		return fileName;
	}

}
