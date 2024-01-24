package edu.duke.fuqua.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import edu.duke.fuqua.db.CreateService;
import edu.duke.fuqua.db.ReadService;
import edu.duke.fuqua.db.UpdateService;
import edu.duke.fuqua.vo.ExcelAcronym;
import edu.duke.fuqua.vo.Tag;

public class PostgresUtils {

	private static String dbName;
	private static Logger log = Logger.getLogger(PostgresUtils.class);

	private static PostgresUtils instance;

	public static PostgresUtils getInstance() {
		if (instance == null) {
			instance = new PostgresUtils();
		}
		return instance;
	}

	public void updateRecCase(Connection connection, Integer id, String columnName, String updateValue) throws Exception {
		try {
			String tableName = ConfigUtils.getProperty("table.name.rec.case");
			List<String> columnNamesList = new ReadService().getColumnsInTable(connection, tableName);
			columnNamesList = columnNamesList.stream().filter(f -> f.compareTo("id") != 0 && f.compareTo("created") != 0).collect(Collectors.toList());

			String sql = "UPDATE " + getDbName() + "." + tableName + " "/**/
					+ " SET " + columnName + " = ? " /**/
					+ " WHERE id = ? " /**/
					+ "; ";

			// log.info(sql);

			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1, updateValue);
			ps.setInt(2, id);

			new UpdateService().update(connection, ps);
		} catch (Exception e) {
			throw e;
		}
	}

	public List<ExcelAcronym> queryFuquaAcronyms(Connection connection, Integer id) throws Exception {

		List<ExcelAcronym> returnList = new ArrayList<>();

		try {
			String sql = "SELECT a.id, a.acronym, a.refers_to, a.definition, a.area_key, a.active FROM " + DdlUtils.dbName("fuqua_acronyms") + " a ";
			if (id != null) {
				sql += " WHERE a.id = " + id.toString() + " ";
			}

			// log.info(sql);
			ResultSet rs = new ReadService().doQuery(connection, sql);
			while (rs.next()) {
				ExcelAcronym ea = new ExcelAcronym(rs.getInt("id"), /**/
						rs.getString("acronym"), /**/
						rs.getString("refers_to"), /**/
						rs.getString("definition"), /**/
						rs.getString("area_key")/**/
				);
				returnList.add(ea);
			}
		} catch (Exception e) {
			throw e;
		}
		return returnList;
	}

	public List<Tag> queryFuquaAcronymTags(Connection connection, Integer id) throws Exception {

		List<Tag> returnList = new ArrayList<>();

		try {
			String sql = "SELECT at.id, at.name, at.active FROM " + DdlUtils.dbName("fuqua_acronym_tags") + " at ";
			if (id != null) {
				sql += " WHERE at.id = " + id.toString() + " ";
			}

			// log.info(sql);
			ResultSet rs = new ReadService().doQuery(connection, sql);
			while (rs.next()) {
				Tag tag = new Tag(rs.getInt("id"), rs.getString("name"), rs.getBoolean("active"));

				returnList.add(tag);
			}
		} catch (Exception e) {
			throw e;
		}
		return returnList;
	}

//	public List<TemplateColumn> queryRecTemplate(Connection connection, Integer caseId) throws Exception {
//
//		List<TemplateColumn> returnList = new ArrayList<>();
//
//		try {
//			String sql = "SELECT t.id, t.case_id, t.column_number, t.column_name, t.is_p_value  "/**/
//					+ " FROM " + DdlUtils.dbName("rec_data_template") + " t, " + DdlUtils.dbName("rec_case") + " c ";
//			sql += " WHERE c.id = t.case_id ";
//
//			if (caseId != null) {
//				sql += " AND c.id = " + caseId.toString() + " ";
//			}
//
//			sql += " ORDER BY c.id, t.column_number "/**/
//			;
//			ResultSet rs = new ReadService().doQuery(connection, sql);
//			while (rs.next()) {
//				returnList.add(new TemplateColumn(rs.getInt("id"), rs.getInt("case_id"), rs.getInt("column_number"), rs.getString("column_name"), rs.getBoolean("is_p_value")));
//			}
//		} catch (Exception e) {
//			throw e;
//		}
//		return returnList;
//	}

//
//	public ResultSet queryResponsesByCase(Connection connection, Integer caseId) throws Exception {
//		try {
//			String sql = "SELECT "/**/
//					+ " s.uid, s.first_name, s.last_name, " /**/
//					+ " c.assignment_id, c.title, "/**/
//					+ " ss.score, "/**/
//					+ " sr.submitted_value_seq_number, sr.submitted_value "/**/
//					+ " from " /**/
//					+ " rec_students s, rec_student_responses sr, rec_student_scores ss, rec_case c "/**/
//					+ " where "/**/
//					// + " c.id = ? "/**/
//					+ " c.id = \'" + String.valueOf(caseId) + "\' "/**/
//					+ " and s.id = sr.user_id "/**/
//					+ " and sr.case_id = c.id "/**/
//					+ " and ss.user_id = sr.user_id "/**/
//					+ " and ss.case_id = c.id "/**/
//					+ " order by c.id, s.uid, sr.submitted_value_seq_number "/**/
//			;
//
//			// PreparedStatement ps = connection.prepareStatement(sql);
//			// ps.setInt(1, caseId);
//
//			return new ReadService().doQuery(connection, sql);
//		} catch (Exception e) {
//			throw e;
//		}
//	}

	public Integer populateFuquaAcronyms(Connection connection, String tableName, List<String> columnNamesList, ExcelAcronym data) throws Exception {
		try {
			String sql = "INSERT INTO " + getDbName() + "." + tableName + " "/**/
					+ " (" + columnNamesList.stream().collect(Collectors.joining(", ")) + " ) " /**/
					+ " VALUES " /**/
					+ " (" + columnNamesList.stream().map(m -> "?").collect(Collectors.joining(", ")) + " ) " /**/
					+ " RETURNING id "/**/
					+ "; ";

			// log.info(sql);
			CreateService service = new CreateService();

			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1, data.getAcronym());
			ps.setString(2, data.getRefersTo());
			ps.setString(3, data.getDefinition());
			ps.setString(4, data.getAreaKey());
			ps.setBoolean(5, data.isActive());
			ps.setString(6, "postgres"); // created_by
			ps.setString(7, null); // deleted_by
			ps.setTimestamp(8, null); // deleted

			// log.info("Inserting to rec_template: " + data.toString());
			Integer id = service.insert(connection, ps);
			return id;
		} catch (Exception e) {
			throw e;
		}
	}

	public Integer populateFuquaAcronymTag(Connection connection, String tableName, List<String> columnNamesList, String tag) throws Exception {
		try {
			String sql = "INSERT INTO " + getDbName() + "." + tableName + " "/**/
					+ " (" + columnNamesList.stream().collect(Collectors.joining(", ")) + " ) " /**/
					+ " VALUES " /**/
					+ " (" + columnNamesList.stream().map(m -> "?").collect(Collectors.joining(", ")) + " ) " /**/
					+ " RETURNING id "/**/
					+ "; ";

			CreateService service = new CreateService();

			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1, tag);
			ps.setBoolean(2, true);
			ps.setString(3, "postgres"); // created_by
			ps.setString(4, null); // deleted_by
			ps.setTimestamp(5, null); // deleted

			Integer id = service.insert(connection, ps);
			return id;
		} catch (Exception e) {
			throw e;
		}
	}

//	public Integer populateRecValue(Connection connection, String tableName, List<String> columnNamesList, TemplateValue data) throws Exception {
//		try {
//			String sql = "INSERT INTO " + getDbName() + "." + tableName + " "/**/
//					+ " (" + columnNamesList.stream().collect(Collectors.joining(", ")) + " ) " /**/
//					+ " VALUES " /**/
//					+ " (" + columnNamesList.stream().map(m -> "?").collect(Collectors.joining(", ")) + " ) " /**/
//					+ " RETURNING id "/**/
//					+ "; ";
//
//			// log.info(sql);
//			CreateService service = new CreateService();
//
//			PreparedStatement ps = connection.prepareStatement(sql);
//			ps.setInt(1, data.getTemplateId());
//			ps.setString(2, data.getDataValue());
//			ps.setString(3, data.getDataType());
//			ps.setBoolean(4, data.getPvalue());
//			ps.setString(5, "postgres");
//			ps.setString(6, null);
//			ps.setTimestamp(7, null);
//
//			// log.info("Inserting to rec_value: " + data.toString());
//			Integer id = service.insert(connection, ps);
//			return id;
//		} catch (Exception e) {
//			throw e;
//		}
//	}

	public static String getDbName() {
		try {
			if (dbName == null) {
				dbName = ConfigUtils.getProperty("db.name");
			}

		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			System.exit(99);
		}

		return dbName;
	}

}
