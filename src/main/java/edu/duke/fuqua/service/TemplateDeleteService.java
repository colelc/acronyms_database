package edu.duke.fuqua.service;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;

import edu.duke.fuqua.db.ConnectionService;
import edu.duke.fuqua.db.UpdateService;
import edu.duke.fuqua.utils.ConfigUtils;
import edu.duke.fuqua.utils.PostgresUtils;
import edu.duke.fuqua.vo.CaseReport;
import edu.duke.fuqua.vo.TemplateColumn;

public class TemplateDeleteService {

	private static Logger log = Logger.getLogger(TemplateDeleteService.class);

	private static TemplateDeleteService instance;

	public static TemplateDeleteService getInstance() {
		if (instance == null) {
			instance = new TemplateDeleteService();
		}
		return instance;
	}

	public boolean deleteStudentResponses(Connection connection, Integer caseId, Integer userId) {
		try {
			String tableName = ConfigUtils.getProperty("table.name.rec.student.responses");

			String sql = "DELETE FROM " + PostgresUtils.getDbName() + "." + tableName + " "/**/
					+ " WHERE case_id = ? AND user_id = ? " /**/
					+ "; ";

			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, caseId);
			ps.setInt(2, userId);
			new UpdateService().update(connection, ps);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public boolean deleteStudentScore(Connection connection, Integer caseId, Integer userId) {
		try {
			String tableName = ConfigUtils.getProperty("table.name.rec.student.scores");

			String sql = "DELETE FROM " + PostgresUtils.getDbName() + "." + tableName + " "/**/
					+ " WHERE case_id = ? AND user_id = ? " /**/
					+ "; ";

			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, caseId);
			ps.setInt(2, userId);
			new UpdateService().update(connection, ps);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public Integer deleteCase(Integer caseId) throws Exception {
		try {
			CaseReport toDelete = CaseReportService.getInstance().getThickCaseReport(caseId);
			if (toDelete == null) {
				throw new Exception("Cannot locate thick case object for caseId=" + String.valueOf(caseId));
			}

			Connection connection = ConnectionService.connect("postgres");
			connection.setAutoCommit(false);

			// delete any student responses and scores
			String table = ConfigUtils.getProperty("table.name.rec.student.scores");
			if (!deleteByCaseId(connection, caseId, table)) {
				connection.rollback();
				throw new Exception("Cannot delete rows from " + table + " table, caseId=" + String.valueOf(caseId));
			}

			table = ConfigUtils.getProperty("table.name.rec.student.responses");
			if (!deleteByCaseId(connection, caseId, table)) {
				connection.rollback();
				throw new Exception("Cannot delete rows from " + table + " table, caseId=" + String.valueOf(caseId));
			}

			// delete the template values for each column
			for (TemplateColumn col : toDelete.getTemplateColumns()) {
				if (!deleteTemplateColumnValues(connection, col.getId())) {
					connection.rollback();
					throw new Exception("Cannot delete rows from the rec_data_values table, caseId=" + String.valueOf(caseId));
				}
			}

			// delete the template columns
			table = ConfigUtils.getProperty("table.name.rec.data.template");
			if (!deleteByCaseId(connection, caseId, table)) {
				connection.rollback();
				throw new Exception("Cannot delete rows from " + table + " table, caseId=" + String.valueOf(caseId));
			}

			// delete the case record
			table = ConfigUtils.getProperty("table.name.rec.case");
			if (!deleteByCaseId(connection, caseId, table, "id")) {
				connection.rollback();
				throw new Exception("Cannot delete rows from " + table + " table, caseId=" + String.valueOf(caseId));
			}

			connection.commit();

			return caseId;
		} catch (Exception e) {
			throw e;
		}
	}

	public void deleteTemplateColumnAndValues(Integer caseId, Integer templateId) throws Exception {
		// select * from rec_data_Template where id = 92 and case_id = 14;
		// select * from rec_data_values where template_id = 92;

		try {
			Connection connection = ConnectionService.connect("postgres");
			connection.setAutoCommit(false);

			if (deleteTemplateColumnValues(connection, templateId)) {
				if (deleteTemplateColumn(connection, templateId, caseId)) {
					connection.commit();
				} else {
					connection.rollback(); // problem in deleteTemplateColumn
				}
				return;
			}

			connection.rollback(); // problem in deleteTemplateColumnValues

		} catch (Exception e) {
			throw e;
		}
	}

	private boolean deleteTemplateColumnValues(Connection connection, Integer templateId) {
		try {
			String tableName = ConfigUtils.getProperty("table.name.rec.data.values");

			String sql = "DELETE FROM " + PostgresUtils.getDbName() + "." + tableName + " "/**/
					+ " WHERE template_id = ? " /**/
					+ "; ";

			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, templateId);
			new UpdateService().update(connection, ps);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	private boolean deleteTemplateColumn(Connection connection, Integer templateId, Integer caseId) {
		try {
			String tableName = ConfigUtils.getProperty("table.name.rec.data.template");

			String sql = "DELETE FROM " + PostgresUtils.getDbName() + "." + tableName + " "/**/
					+ " WHERE id = ? AND case_id = ? " /**/
					+ "; ";

			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, templateId);
			ps.setInt(2, caseId);
			new UpdateService().update(connection, ps);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	private boolean deleteByCaseId(Connection connection, Integer caseId, String tableName, String... columnName) {
		try {
			String column = columnName.length == 0 ? "case_id" : columnName[0];
			String sql = "DELETE FROM " + PostgresUtils.getDbName() + "." + tableName + " "/**/
					+ " WHERE " + column + " = ? " /**/
					+ "; ";

			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, caseId);
			new UpdateService().update(connection, ps);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

}
