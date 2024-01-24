package edu.duke.fuqua.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import edu.duke.fuqua.db.ConnectionService;
import edu.duke.fuqua.db.CreateService;
import edu.duke.fuqua.db.ReadService;
import edu.duke.fuqua.db.UpdateService;
import edu.duke.fuqua.utils.ConfigUtils;
import edu.duke.fuqua.utils.DdlUtils;
import edu.duke.fuqua.utils.NumberUtils;
import edu.duke.fuqua.utils.PostgresUtils;
import edu.duke.fuqua.vo.TemplateColumn;
import edu.duke.fuqua.vo.thin.TemplateValueThin;

public class TemplateUpdateService {
	private static Logger log = Logger.getLogger(TemplateUpdateService.class);

	private static TemplateUpdateService instance;

	public static TemplateUpdateService getInstance() {
		if (instance == null) {
			instance = new TemplateUpdateService();
		}
		return instance;
	}

	public void updateTemplateTitle(Integer caseId, String title) throws Exception {
		try {
			Connection connection = ConnectionService.connect("postgres");

			String tableName = ConfigUtils.getProperty("table.name.rec.case");
			List<String> columnNamesList = new ReadService().getColumnsInTable(connection, tableName);
			columnNamesList = columnNamesList.stream().filter(f -> f.compareTo("id") != 0 && f.compareTo("created") != 0).collect(Collectors.toList());

			String sql = "UPDATE " + PostgresUtils.getDbName() + "." + tableName + " "/**/
					+ " SET title = ?, short_title = ? " /**/
					+ " WHERE id = ? " /**/
					+ "; ";

			log.info(sql);

			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1, title);
			ps.setString(2, title);
			ps.setInt(3, caseId);

			new UpdateService().update(connection, ps);
		} catch (Exception e) {
			throw e;
		}
	}

	public void updateTemplateValues(Integer caseId, List<TemplateValueThin> updates) throws Exception {
		try {
			Connection connection = ConnectionService.connect("postgres");

			String tableName = ConfigUtils.getProperty("table.name.rec.data.values");

			for (TemplateValueThin tvt : updates) {

				String sql = "UPDATE " + PostgresUtils.getDbName() + "." + tableName + " "/**/
						+ " SET data_value = ? " /**/
						+ " WHERE id = ? AND template_id = ? " /**/
						+ "; ";

				log.info(sql);

				PreparedStatement ps = connection.prepareStatement(sql);
				ps.setString(1, tvt.getDataValue());
				ps.setInt(2, tvt.getId());
				ps.setInt(3, tvt.getTemplateId());

				new UpdateService().update(connection, ps);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public void newPvalues(Integer caseId, List<String> values, List<TemplateValueThin> existingPvalues) throws Exception {
		try {
			Connection connection = ConnectionService.connect("postgres");

			String tableName = ConfigUtils.getProperty("table.name.rec.data.values");

			for (int i = 0; i < values.size(); i++) {
				String newPvalue = values.get(i);
				Integer templateValueId = existingPvalues.get(i).getId();
				Integer templateId = existingPvalues.get(i).getTemplateId();

				String sql = "UPDATE " + PostgresUtils.getDbName() + "." + tableName + " "/**/
						+ " SET data_value = ? " /**/
						+ " WHERE id = ? AND template_id = ? " /**/
						+ "; ";

				// log.info(sql);

				PreparedStatement ps = connection.prepareStatement(sql);
				ps.setInt(1, Integer.valueOf(newPvalue));
				ps.setInt(2, templateValueId);
				ps.setInt(3, templateId);

				new UpdateService().update(connection, ps);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public void addTemplateColumn(Integer caseId, String columnName, List<String> values) throws Exception {
		// select * from rec_case where id = 14;
		// select * from rec_data_template where case_id = 14;
		// select * from rec_data_values where template_id between <start template id>
		// and <end template id>

		try {
			Connection connection = ConnectionService.connect("postgres");
			connection.setAutoCommit(false);

			PostgresUtils instance = PostgresUtils.getInstance();

			List<TemplateColumn> columns = instance.queryRecTemplate(connection, caseId); // (these are ordered by column number)
			TemplateColumn pvColumn = columns.stream().skip(columns.size() - 1).findFirst().get();

			if (pvColumn.getPvalue().compareTo(Boolean.FALSE) == 0) { // Denver case
				Integer newTemplateId = addTemplateColumnName(connection, caseId, pvColumn.getColumnNumber() + 1, columnName);
				if (newTemplateId != null) {
					if (addTemplateColumnValues(connection, newTemplateId, values)) {
						connection.commit();
					} else {
						connection.rollback(); // problem in deleteTemplateColumn
					}
					return;
				}
				connection.rollback(); // problem in deleteTemplateColumnValues
			} else { // Barcelona case
				if (updateRecTemplate(connection, pvColumn.getId(), "column_number", pvColumn.getColumnNumber() + 1)) {
					Integer newTemplateId = addTemplateColumnName(connection, caseId, pvColumn.getColumnNumber(), columnName);
					if (newTemplateId != null) {
						if (addTemplateColumnValues(connection, newTemplateId, values)) {
							connection.commit();
						} else {
							connection.rollback(); // problem in deleteTemplateColumn
						}
						return;
					}
				}
				connection.rollback(); // problem in deleteTemplateColumnValues
			}
		} catch (Exception e) {
			throw e;
		}
	}

	private Integer addTemplateColumnName(Connection connection, Integer caseId, Integer columnNumber, String columnName) {
		try {
			String tableName = ConfigUtils.getProperty("table.name.rec.data.template");
			List<String> columnNamesList = DdlUtils.getTableColumns(connection, tableName);

			String sql = "INSERT INTO " + PostgresUtils.getDbName() + "." + tableName + " "/**/
					+ " (" + columnNamesList.stream().collect(Collectors.joining(", ")) + " ) " /**/
					+ " VALUES " /**/
					+ " (" + columnNamesList.stream().map(m -> "?").collect(Collectors.joining(", ")) + " ) " /**/
					+ " RETURNING id "/**/
					+ "; ";

			// log.info(sql);
			CreateService service = new CreateService();

			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, caseId);
			ps.setInt(2, columnNumber);
			ps.setString(3, columnName);
			ps.setBoolean(4, Boolean.FALSE);
			ps.setString(5, "postgres");
			ps.setString(6, null);
			ps.setTimestamp(7, null);

			// log.info("Inserting to rec_template: " + data.toString());
			Integer id = service.insert(connection, ps);
			return id;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	private boolean addTemplateColumnValues(Connection connection, Integer templateId, List<String> values) {
		try {
			String tableName = ConfigUtils.getProperty("table.name.rec.data.values");
			List<String> columnNamesList = DdlUtils.getTableColumns(connection, tableName);

			for (String value : values) {

				String sql = "INSERT INTO " + PostgresUtils.getDbName() + "." + tableName + " "/**/
						+ " (" + columnNamesList.stream().collect(Collectors.joining(", ")) + " ) " /**/
						+ " VALUES " /**/
						+ " (" + columnNamesList.stream().map(m -> "?").collect(Collectors.joining(", ")) + " ) " /**/
						+ " RETURNING id "/**/
						+ "; ";

				// log.info(sql);
				CreateService service = new CreateService();

				PreparedStatement ps = connection.prepareStatement(sql);
				ps.setInt(1, templateId);
				ps.setString(2, value);
				ps.setString(3, NumberUtils.assignDataType(value));
				ps.setBoolean(4, Boolean.FALSE);
				ps.setString(5, "postgres");
				ps.setString(6, null);
				ps.setTimestamp(7, null);

				// log.info("Inserting to rec_value: " + data.toString());
				Integer id = service.insert(connection, ps);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean updateRecTemplate(Connection connection, Integer id, String targetColumn, Integer updateValue) throws Exception {
		try {
			String tableName = ConfigUtils.getProperty("table.name.rec.data.template");

			String sql = "UPDATE " + PostgresUtils.getDbName() + "." + tableName + " "/**/
					+ " SET " + targetColumn + " = ? " /**/
					+ " WHERE id = ? " /**/
					+ "; ";

			// log.info(sql);

			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, updateValue);
			ps.setInt(2, id);

			new UpdateService().update(connection, ps);
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

//	public void deleteTemplateColumn(Integer caseId, Integer templateId) throws Exception {
//		// select * from rec_data_Template where id = 92 and case_id = 14;
//		// select * from rec_data_values where template_id = 92;
//
//		try {
//			Connection connection = ConnectionService.connect("postgres");
//			connection.setAutoCommit(false);
//
//			if (deleteTemplateColumnValues(connection, templateId)) {
//				if (deleteTemplateColumn(connection, templateId, caseId)) {
//					connection.commit();
//				} else {
//					connection.rollback(); // problem in deleteTemplateColumn
//				}
//				return;
//			}
//
//			connection.rollback(); // problem in deleteTemplateColumnValues
//
//		} catch (Exception e) {
//			throw e;
//		}
//	}
//
//	private boolean deleteTemplateColumnValues(Connection connection, Integer templateId) {
//		try {
//			String tableName = ConfigUtils.getProperty("table.name.rec.data.values");
//
//			String sql = "DELETE FROM " + PostgresUtils.getDbName() + "." + tableName + " "/**/
//					+ " WHERE template_id = ? " /**/
//					+ "; ";
//
//			log.info(sql);
//
//			PreparedStatement ps = connection.prepareStatement(sql);
//			ps.setInt(1, templateId);
//			new UpdateService().update(connection, ps);
//			return true;
//		} catch (Exception e) {
//			log.error(e.getMessage());
//			e.printStackTrace();
//			return false;
//		}
//	}
//
//	private boolean deleteTemplateColumn(Connection connection, Integer templateId, Integer caseId) {
//		try {
//			String tableName = ConfigUtils.getProperty("table.name.rec.data.template");
//
//			String sql = "DELETE FROM " + PostgresUtils.getDbName() + "." + tableName + " "/**/
//					+ " WHERE id = ? AND case_id = ? " /**/
//					+ "; ";
//
//			log.info(sql);
//
//			PreparedStatement ps = connection.prepareStatement(sql);
//			ps.setInt(1, templateId);
//			ps.setInt(2, caseId);
//			new UpdateService().update(connection, ps);
//			return true;
//		} catch (Exception e) {
//			log.error(e.getMessage());
//			e.printStackTrace();
//			return false;
//		}
//	}

}
