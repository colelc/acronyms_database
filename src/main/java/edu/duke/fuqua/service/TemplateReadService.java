package edu.duke.fuqua.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.duke.fuqua.db.ConnectionService;
import edu.duke.fuqua.db.ReadService;
import edu.duke.fuqua.utils.DdlUtils;
import edu.duke.fuqua.vo.Student;
import edu.duke.fuqua.vo.thin.TemplateColumnThin;
import edu.duke.fuqua.vo.thin.TemplateLineItem;
import edu.duke.fuqua.vo.thin.TemplateValueThin;

public class TemplateReadService {
	private static Logger log = Logger.getLogger(TemplateReadService.class);

	private static TemplateReadService instance;

	public static TemplateReadService getInstance() {
		if (instance == null) {
			instance = new TemplateReadService();
		}
		return instance;
	}

	public Integer getTemplateCaseId(String courseId, String assignmentId) throws Exception {
		try {
			Connection connection = ConnectionService.connect("postgres");

			String sql = "SELECT id FROM " + DdlUtils.dbName("rec_case") + " WHERE ";
			sql += " assignment_id = " + assignmentId + " ";
			if (courseId != null) {
				sql += " AND course_id = " + courseId + " ";
			}
			ResultSet rs = new ReadService().doQuery(connection, sql);
			while (rs.next()) {
				return rs.getInt("id");
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

	public List<Student> queryRecStudents(Connection connection, String uid) throws Exception {

		List<Student> returnList = new ArrayList<>();

		try {
			String sql = "SELECT id, uid, first_name, last_name, preferred_first_name, preferred_last_name FROM " + DdlUtils.dbName("rec_students");
			sql += " WHERE uid = " + "\'" + uid.trim() + "\' ";
			ResultSet rs = new ReadService().doQuery(connection, sql);
			while (rs.next()) {
				returnList.add(new Student(rs.getInt("id"), /**/
						rs.getString("uid"), /**/
						rs.getString("first_name"), /**/
						rs.getString("last_name"), /**/
						rs.getString("preferred_first_name"), /**/
						rs.getString("preferred_last_name")));
			}
		} catch (Exception e) {
			throw e;
		}
		return returnList;
	}

	public List<Integer> queryIfExistsRecStudentResponses(Connection connection, Integer caseId, Integer userId) throws Exception {

		List<Integer> returnList = new ArrayList<>();

		try {
			String sql = "SELECT "/**/
					+ " id "/**/
					+ " from " /**/
					+ " rec_student_responses "/**/
					+ " where "/**/
					+ " case_id = " + String.valueOf(caseId) + " "/**/
					+ " and user_id = " + String.valueOf(userId) + " ";

			ResultSet rs = new ReadService().doQuery(connection, sql);
			while (rs.next()) {
				returnList.add(rs.getInt("id"));
			}

		} catch (Exception e) {
			throw e;
		}
		return returnList;
	}

	public List<TemplateColumnThin> getTemplateColumns(Integer caseId) throws Exception {
		try {
			return CaseLoader.getTemplateColumns(caseId);
		} catch (Exception e) {
			throw e;
		}
	}

	public List<TemplateValueThin> getPValues(Integer caseId) throws Exception {
		try {
			return CaseLoader.getPValues(caseId);
		} catch (Exception e) {
			throw e;
		}
	}

	public List<TemplateLineItem> getTemplateLineItemsForEdit(Integer caseId) throws Exception {
		try {
			return CaseLoader.getTemplateLineItemsForEdit(caseId);
		} catch (Exception e) {
			throw e;
		}
	}
}
