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
import edu.duke.fuqua.vo.BoardMember;
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

	public Integer populateFuquaAcronymPermissions(Connection connection, String tableName, List<String> columnNamesList, String dukeId, String email) throws Exception {
		try {
			String sql = "INSERT INTO " + getDbName() + "." + tableName + " "/**/
					+ " (" + columnNamesList.stream().collect(Collectors.joining(", ")) + " ) " /**/
					+ " VALUES " /**/
					+ " (" + columnNamesList.stream().map(m -> "?").collect(Collectors.joining(", ")) + " ) " /**/
					+ " RETURNING id "/**/
					+ "; ";

			CreateService service = new CreateService();

			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1, dukeId);
			ps.setString(2, email);
			ps.setBoolean(3, false); // send_email
			ps.setBoolean(4, true); // active
			ps.setString(5, "postgres"); // created_by

			Integer id = service.insert(connection, ps);
			return id;
		} catch (Exception e) {
			throw e;
		}
	}

	public Integer populateDARBoardMembers(Connection connection, String tableName, List<String> columnNamesList, BoardMember data) throws Exception {
		try {
			String sql = "INSERT INTO " + getDbName() + "." + tableName + " "/**/
					+ " (" + columnNamesList.stream().collect(Collectors.joining(", ")) + " ) " /**/
					+ " VALUES " /**/
					+ " (" + columnNamesList.stream().map(m -> "?").collect(Collectors.joining(", ")) + " ) " /**/
					+ " RETURNING id "/**/
					+ "; ";

			log.info(sql);
			CreateService service = new CreateService();

			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, data.getEntityId());
			ps.setString(2, data.getBoardFname() == null ? "" : data.getBoardFname().trim());
			ps.setString(3, data.getBoardLname() == null ? "" : data.getBoardLname().trim());
			ps.setString(4, data.getBoardPreferredName() == null ? "" : data.getBoardPreferredName().trim());
			ps.setString(5, data.getProgram() == null ? "" : data.getProgram().trim());
			ps.setString(6, data.getBoardClass() == null ? "" : data.getBoardClass().trim());
			ps.setString(7, data.getHsmCert() == null ? "" : data.getHsmCert().trim());
			ps.setString(8, data.getOtherDukeDegree() == null ? "" : data.getOtherDukeDegree().trim());
			ps.setString(9, data.getEmployer() == null ? "" : data.getEmployer().trim());
			ps.setString(10, data.getJobTitle() == null ? "" : data.getJobTitle().trim());
			ps.setString(11, data.getLinkedIn() == null ? "" : data.getLinkedIn());
			ps.setString(12, data.getCurServeOn() == null ? "" : data.getCurServeOn());
			ps.setString(13, data.getBoardPhoto() == null ? "" : data.getBoardPhoto().trim());
			ps.setString(14, data.getBoardEmail() == null ? "" : data.getBoardEmail().trim());

			ps.setBoolean(15, true); // active
			ps.setString(16, "postgres"); // created_by

			// log.info("Inserting to rec_template: " + data.toString());
			Integer id = service.insert(connection, ps);
			return id;
		} catch (Exception e) {
			throw e;
		}
	}

	public Integer queryDARBoardMembersByEntityId(Connection connection, Integer entityId) throws Exception {

		Integer retValue = null;
		List<Integer> uhohList = new ArrayList<>();

		try {
			String sql = "SELECT id FROM " + DdlUtils.dbName("dar_board_members") + " ";
			sql += " WHERE entity_id = " + entityId + " ";

			// log.info(sql);
			ResultSet rs = new ReadService().doQuery(connection, sql);
			while (rs.next()) {
				retValue = rs.getInt("id");
				uhohList.add(retValue);
				if (uhohList.size() > 1) {
					throw new Exception("Entity ID values are not unique");
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return retValue;
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

	public Integer populateFuquaAcronyms(Connection connection, String tableName, List<String> columnNamesList, ExcelAcronym data) throws Exception {
		try {
			String sql = "INSERT INTO " + getDbName() + "." + tableName + " "/**/
					+ " (" + columnNamesList.stream().collect(Collectors.joining(", ")) + " ) " /**/
					+ " VALUES " /**/
					+ " (" + columnNamesList.stream().map(m -> "?").collect(Collectors.joining(", ")) + " ) " /**/
					+ " RETURNING id "/**/
					+ "; ";

			log.info(sql);
			CreateService service = new CreateService();

			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1, data.getAcronym());
			ps.setString(2, data.getRefersTo());
			ps.setString(3, data.getDefinition());
			ps.setString(4, data.getAreaKey());
			ps.setString(5, data.getTagString());
			ps.setBoolean(6, data.isActive());
			ps.setString(7, "postgres"); // created_by
			// ps.setString(8, "postgres"); // last_updated_by

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
			// ps.setString(4, "postgres"); // last_updated_by

			Integer id = service.insert(connection, ps);
			return id;
		} catch (Exception e) {
			throw e;
		}
	}

	public Integer populateFuquaAcronymTagMap(Connection connection, String tableName, List<String> columnNamesList, Integer acronymId, Integer tagId) throws Exception {
		try {
			String sql = "INSERT INTO " + getDbName() + "." + tableName + " "/**/
					+ " (" + columnNamesList.stream().collect(Collectors.joining(", ")) + " ) " /**/
					+ " VALUES " /**/
					+ " (" + columnNamesList.stream().map(m -> "?").collect(Collectors.joining(", ")) + " ) " /**/
					+ " RETURNING id "/**/
					+ "; ";

			CreateService service = new CreateService();

			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, acronymId);
			ps.setInt(2, tagId);
			ps.setBoolean(3, true);
			ps.setString(4, "postgres"); // created_by
			// ps.setString(5, "postgres"); // last_updated_by

			Integer id = service.insert(connection, ps);
			return id;
		} catch (Exception e) {
			throw e;
		}
	}

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
