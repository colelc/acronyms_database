package edu.duke.fuqua.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import edu.duke.fuqua.db.ConnectionService;
import edu.duke.fuqua.excel.ExcelService;
import edu.duke.fuqua.vo.Case;
import edu.duke.fuqua.vo.CaseReport;
import edu.duke.fuqua.vo.ResponseValue;
import edu.duke.fuqua.vo.Student;
import edu.duke.fuqua.vo.StudentResponse;
import edu.duke.fuqua.vo.StudentScore;
import edu.duke.fuqua.vo.TemplateColumn;
import edu.duke.fuqua.vo.TemplateValue;
import edu.duke.fuqua.vo.thin.TemplateLineItem;
import edu.duke.fuqua.vo.thin.TemplateValueThin;

public class QueryUtils {

	private static Logger log = Logger.getLogger(QueryUtils.class);

	public static void queryTests() throws Exception {
		try {
			Connection connection = ConnectionService.connect("postgres");
			PostgresUtils service = new PostgresUtils();

			List<CaseReport> reports = generateCaseReports(connection, service, null);

			for (CaseReport report : reports) {
				log.info("CASE --> " + String.valueOf(report.getCaseRec().getId()) + " " + String.valueOf(report.getCaseRec().getAssignmentId()) + " " + report.getCaseRec().getTitle());

				log.info("SCORES");

				log.info("STUDENT RESPONSES");
			}

			new ExcelService().writeDataToExcel(reports);
		} catch (Exception e) {
			throw e;
		}
	}

	public static List<CaseReport> generateCaseReports(Connection connection, PostgresUtils service, Integer caseId) throws Exception {
		try {
			List<Case> cases = service.queryRecCase(connection, caseId);
			Map<Integer, List<TemplateColumn>> templateColumnMap = queryCaseTemplate(connection, caseId);
			Map<Integer, List<TemplateLineItem>> templateLineItemsMap = constructTemplateLineItems(connection, cases, templateColumnMap, caseId);
			Map<Integer, List<TemplateValue>> pValueMap = queryPValues(connection, caseId);
			Map<Integer, List<StudentResponse>> studentResponses = queryStudentResponses(connection, service, caseId);
			Map<Integer, List<StudentScore>> studentScores = queryStudentScores(connection, service, caseId);

			List<CaseReport> reports = new ArrayList<>();

			for (Case thisCase : cases) {
				log.info("Generating report for: " + thisCase.toString());
				reports.add(new CaseReport(/**/
						thisCase, /**/
						templateLineItemsMap.get(thisCase.getId()), /**/
						templateColumnMap.get(thisCase.getId()), /**/
						pValueMap.get(thisCase.getId()), /**/
						studentResponses.get(thisCase.getId()), /**/
						studentScores.get(thisCase.getId())/**/
				));
			}

			return reports;
		} catch (Exception e) {
			throw e;
		}
	}

	private static Map<Integer, List<TemplateLineItem>> constructTemplateLineItems(Connection connection, /**/
			List<Case> cases, /**/
			Map<Integer, List<TemplateColumn>> templateColumnMap, /**/
			Integer singleCaseId) throws Exception {

		Map<Integer, List<TemplateLineItem>> retMap = new HashMap<>();
		try {
			ResultSet rs = PostgresUtils.getInstance().queryForTemplateLineItems(connection, singleCaseId);

			Map<Integer, List<Map<String, Object>>> caseMap = new HashMap<>();
			List<Map<String, Object>> list = new ArrayList<>();
			Integer prevCaseId = null;

			while (rs.next()) {
				Map<String, Object> map = new HashMap<>();

				Integer id = rs.getInt("caseid");
				map.put("id", id);

				int numberOfDataPoints = rs.getInt("number_of_data_points");
				map.put("numberOfDataPoints", Integer.valueOf(numberOfDataPoints));

				map.put("templateId", rs.getInt("tid"));
				map.put("templateValueId", rs.getInt("tvid"));
				map.put("columnNumber", rs.getInt("column_number"));
				map.put("columnName", rs.getString("column_name"));
				map.put("dataType", rs.getString("data_type"));
				map.put("dataValue", rs.getString("data_value"));

				if (prevCaseId == null) {
					prevCaseId = new Integer(id);
					list = new ArrayList<>();
					list.add(map);
					continue;
				}

				if (id.compareTo(prevCaseId) == 0) {
					list.add(map);
					continue;
				}

				// new case
				caseMap.put(prevCaseId, list);

				list = new ArrayList<>();
				list.add(map);
				prevCaseId = new Integer(id);
			}

			// last entry
			if (prevCaseId != null) {
				caseMap.put(prevCaseId, list);
			}

			// ok, now, we need to flatten this thing, ugh yuck
			for (Integer caseId : caseMap.keySet()) {
				// log.info("--------------- caseid = " + String.valueOf(caseId));
				Map<Integer, List<Map<String, Object>>> valuesMap = new HashMap<>();

				List<Map<String, Object>> items = caseMap.get(caseId);

				// number of data points (rows) for this case
				int numberOfDataPoints = (items.stream().map(m -> (Integer) m.get("numberOfDataPoints")).findFirst().get()).intValue();
				// log.info("numberOfDataPoints=" + numberOfDataPoints);

				// column number indexes
				List<Integer> cols = items.stream().map(m -> (Integer) m.get("columnNumber")).sorted((s1, s2) -> s1.compareTo(s2)).collect(Collectors.toList());
				Set<Integer> columnNumberSet = cols.stream().collect(Collectors.toSet());
				List<Integer> columnNumberList = columnNumberSet.stream().sorted((s1, s2) -> s1.compareTo(s2)).collect(Collectors.toList());
//				for (Integer l : columnNumberList) {
//					log.info(String.valueOf(l));
//				}

				for (Integer columnNumber : columnNumberList) {
					List<Map<String, Object>> sortedValues = items.stream()/**/
							.filter(item -> ((Integer) item.get("columnNumber")).compareTo(columnNumber) == 0)/**/
							.sorted((s1, s2) -> ((Integer) s1.get("templateValueId")).compareTo((Integer) s2.get("templateValueId")))/**/
							.collect(Collectors.toList());
					valuesMap.put(columnNumber, sortedValues);
				}

//				for (Integer colNum : valuesMap.keySet()) {
//					List<Map<String, Object>> lst = (List<Map<String, Object>>) valuesMap.get(colNum);
//					for (Map<String, Object> m : lst) {
//						log.info(String.valueOf(colNum) + " -> " + m.toString());
//					}
//				}

				List<TemplateLineItem> liList = new ArrayList<>();
				int lineItemCounter = 1;

				for (int row = 0; row < numberOfDataPoints; row++) {
					TemplateLineItem lineItem = new TemplateLineItem(String.valueOf(lineItemCounter++));
					List<TemplateValueThin> lineItemValues = new ArrayList<>();

					for (int col = 0; col < columnNumberList.size(); col++) {
						Integer columnIndex = columnNumberList.get(col);

						Map<String, Object> m = valuesMap.get(columnIndex).get(row);
						TemplateValueThin thin = new TemplateValueThin(/**/
								(Integer) m.get("templateValueId"), /**/
								(Integer) m.get("templateId"), /**/
								(String) m.get("dataValue"), /**/
								(String) m.get("dataType"), /**/
								Boolean.FALSE);
						// log.info("Row " + row + " Col " + col + " (index) " +
						// String.valueOf(columnIndex) + " -> " + thin.toString());

						lineItemValues.add(thin);
					}

					lineItem.setValueList(lineItemValues);
					liList.add(lineItem);
				}

				retMap.put(caseId, liList);

				// set the number of columns on the case record
				cases.stream().filter(f -> f.getId().compareTo(caseId) == 0).findFirst().get().setNumberOfTemplateColumns(Integer.valueOf(columnNumberList.size()));
			}
		} catch (Exception e) {
			throw e;
		}
		return retMap;
	}

	private static Map<Integer, List<TemplateColumn>> queryCaseTemplate(Connection connection, Integer caseId) throws Exception {
		try {
			Map<Integer, List<TemplateColumn>> retMap = new HashMap<>();
			List<TemplateColumn> records = PostgresUtils.getInstance().queryRecTemplate(connection, caseId);
			Integer prevCaseId = null;
			List<TemplateColumn> list = new ArrayList<>();

			for (TemplateColumn rec : records) {
				if (prevCaseId == null) {
					prevCaseId = new Integer(rec.getCaseId());
					rec.getValues().addAll(queryCaseTemplateValues(connection, rec));
					list.add(rec);
					continue;
				}

				if (prevCaseId.compareTo(rec.getCaseId()) == 0) {
					rec.getValues().addAll(queryCaseTemplateValues(connection, rec));
					list.add(rec);
					continue;
				}

				retMap.put(prevCaseId, list);
				list = new ArrayList<>();
				rec.getValues().addAll(queryCaseTemplateValues(connection, rec));
				list.add(rec);
				prevCaseId = new Integer(rec.getCaseId());
			}

			// last one
			retMap.put(prevCaseId, list);

			return retMap;
		} catch (Exception e) {
			throw e;
		}
	}

	private static List<TemplateValue> queryCaseTemplateValues(Connection connection, TemplateColumn column) throws Exception {
		try {
			Set<Integer> idSet = new HashSet<>();
			idSet.add(column.getId());
			return PostgresUtils.getInstance().queryRecTemplateValues(connection, idSet);
		} catch (Exception e) {
			throw e;
		}
	}

	private static Map<Integer, List<TemplateValue>> queryPValues(Connection connection, Integer caseId) throws Exception {
		try {
			Map<Integer, List<TemplateValue>> pValueMap = new HashMap<>();
			ResultSet rs = PostgresUtils.getInstance().queryPValuesByCase(connection, caseId);

			Integer prevCaseId = null;
			List<TemplateValue> pvList = new ArrayList<>();

			while (rs.next()) {
				Integer id = rs.getInt("case_id"); // this is a caseId
				Integer templateId = rs.getInt("template_id");
				Integer templateValueId = rs.getInt("template_value_id");
				String dataValue = rs.getString("data_value");
				String dataType = rs.getString("data_type");

				if (prevCaseId == null) { // 1st rec
					prevCaseId = new Integer(id);
					pvList.add(new TemplateValue(templateValueId, templateId, dataValue, dataType, Boolean.TRUE));
					continue;
				}

				if (id.compareTo(prevCaseId) == 0) { // accumulating p-Values for this case
					pvList.add(new TemplateValue(templateValueId, templateId, dataValue, dataType, Boolean.TRUE));
					continue;
				}

				// new case
				pValueMap.put(prevCaseId, pvList);

				pvList = new ArrayList<>();
				pvList.add(new TemplateValue(templateValueId, templateId, dataValue, dataType, Boolean.TRUE));
				prevCaseId = new Integer(id);
			}

			// last entry
			pValueMap.put(prevCaseId, pvList);

			// pValueMap.forEach((k, values) -> {
			// values.forEach(v -> log.info(String.valueOf(k) + " -> " + v.toString()));
			// });

			return pValueMap;
		} catch (Exception e) {
			throw e;
		}
	}

	private static Map<Integer, List<StudentResponse>> queryStudentResponses(Connection connection, PostgresUtils service, /**/
			Integer singleCaseId) throws Exception {

		Map<Integer, List<StudentResponse>> caseMap = new HashMap<>();

		try {
			ResultSet rs = service.queryResponses(connection, singleCaseId);

			Integer prevCaseId = null;
			Integer prevUserId = null;
			StudentResponse studentResponse = new StudentResponse();
			List<ResponseValue> responseValues = new ArrayList<>();
			List<StudentResponse> studentResponsesForThisCase = new ArrayList<>();

			while (rs.next()) {
				Integer caseId = rs.getInt("case_id");
				Integer userId = rs.getInt("user_id");
				String uid = rs.getString("uid");
				String firstName = rs.getString("first_name");
				String lastName = rs.getString("last_name");
				String preferredFirstName = rs.getString("preferred_first_name") == null ? "" : rs.getString("preferred_first_name");
				String preferredLastName = rs.getString("preferred_last_name") == null ? "" : rs.getString("preferred_last_name");

				Integer assignmentId = rs.getInt("assignment_id");
				Integer studentResponseId = rs.getInt("student_response_id");
				Integer seq = rs.getInt("submitted_value_seq_number");
				Float submittedValue = rs.getFloat("submitted_value");

				if (prevCaseId == null) { // 1st rec
					prevCaseId = new Integer(caseId);
					prevUserId = new Integer(userId);

					responseValues.add(new ResponseValue(seq, NumberUtils.assignDataType(String.valueOf(submittedValue)), String.valueOf(submittedValue), submittedValue));
					studentResponse = new StudentResponse(studentResponseId, /**/
							caseId, /**/
							new Student(userId, uid, firstName, lastName, preferredFirstName, preferredLastName));
					continue;
				}

				if (caseId.compareTo(prevCaseId) == 0) { // same case
					if (userId.compareTo(prevUserId) == 0) { // same user
						responseValues.add(new ResponseValue(seq, NumberUtils.assignDataType(String.valueOf(submittedValue)), String.valueOf(submittedValue), submittedValue));
						continue;
					}

					// same case, new user
					studentResponse.setResponseValues(responseValues);
					studentResponsesForThisCase.add(studentResponse);

					responseValues = new ArrayList<>();
					responseValues.add(new ResponseValue(seq, NumberUtils.assignDataType(String.valueOf(submittedValue)), String.valueOf(submittedValue), submittedValue));

					studentResponse = new StudentResponse(studentResponseId, /**/
							caseId, /**/
							new Student(userId, uid, firstName, lastName, preferredFirstName, preferredLastName));

					prevUserId = new Integer(userId);
					continue;
				}

				// new case, and therefore, new user
				studentResponse.setResponseValues(responseValues);
				studentResponsesForThisCase.add(studentResponse);
				caseMap.put(prevCaseId, studentResponsesForThisCase);

				responseValues = new ArrayList<>();
				studentResponse = new StudentResponse(studentResponseId, caseId, new Student(userId, uid, firstName, lastName, preferredFirstName, preferredLastName));
				responseValues.add(new ResponseValue(seq, NumberUtils.assignDataType(String.valueOf(submittedValue)), String.valueOf(submittedValue), submittedValue));

				studentResponsesForThisCase = new ArrayList<>();

				prevUserId = new Integer(userId);
				prevCaseId = new Integer(caseId);
			}

			// handle final entry
			// userMap.put(prevUserId, responseList);
			studentResponse.setResponseValues(responseValues);
			studentResponsesForThisCase.add(studentResponse);
			caseMap.put(prevCaseId, studentResponsesForThisCase);

		} catch (Exception e) {
			throw e;
		}
		return caseMap;
	}

	private static Map<Integer, List<StudentScore>> queryStudentScores(Connection connection, PostgresUtils service, /**/
			Integer singleCaseId) throws Exception {
		// caseId -> student scores list
		Map<Integer, List<StudentScore>> retMap = new HashMap<>();

		try {
			List<StudentScore> scores = new ArrayList<>();
			Integer prevCaseId = null;

			ResultSet rs = service.queryScores(connection, singleCaseId);
			while (rs.next()) {
				Integer caseId = rs.getInt("case_id");
				Integer assignmentId = rs.getInt("assignment_id");

				Integer userId = rs.getInt("user_id");
				String uid = rs.getString("uid");
				String firstName = rs.getString("first_name");
				String lastName = rs.getString("last_name");
				String preferredFirstName = rs.getString("preferred_first_name") == null ? "" : rs.getString("preferred_first_name");
				String preferredLastName = rs.getString("preferred_last_name") == null ? "" : rs.getString("preferred_last_name");
				Student student = new Student(userId, uid, firstName, lastName, preferredFirstName, preferredLastName);

				Integer studentScoreId = rs.getInt("student_score_id");
				Float score = rs.getFloat("score");
				Timestamp created = rs.getTimestamp("created");

				if (prevCaseId == null) { // 1st record
					prevCaseId = new Integer(caseId);
					scores.add(new StudentScore(studentScoreId, caseId, student, score, created));
					continue;
				}

				if (caseId.compareTo(prevCaseId) == 0) { // same case
					scores.add(new StudentScore(studentScoreId, caseId, student, score, created));
					continue;
				}

				// new case
				retMap.put(prevCaseId, scores);

				scores = new ArrayList<>();
				scores.add(new StudentScore(studentScoreId, caseId, student, score, created));
				prevCaseId = new Integer(caseId);

//				log.info(String.valueOf(caseId)/**/
//						+ " " + uid + " " + firstName + " " + lastName /**/
//						+ " " + String.valueOf(assignmentId) /**/
//						+ " " + String.valueOf(score)/**/
//				);
			}

			// final score
			retMap.put(prevCaseId, scores);
		} catch (Exception e) {
			throw e;
		}
		return retMap;
	}
}
