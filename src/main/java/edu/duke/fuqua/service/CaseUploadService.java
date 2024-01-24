package edu.duke.fuqua.service;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.duke.fuqua.utils.ConfigUtils;
import edu.duke.fuqua.utils.DdlUtils;
import edu.duke.fuqua.utils.NumberUtils;
import edu.duke.fuqua.utils.PostgresUtils;
import edu.duke.fuqua.vo.Case;
import edu.duke.fuqua.vo.TemplateColumn;
import edu.duke.fuqua.vo.TemplateValue;

public class CaseUploadService {
	private static CaseUploadService instance;
	private static Logger log = Logger.getLogger(CaseUploadService.class);

	public static CaseUploadService getInstance() {
		if (instance == null) {
			instance = new CaseUploadService();
		}
		return instance;
	}

	private Integer populateCaseRec(Connection connection, int numberOfDataPoints, String title) throws Exception {
		try {
			Integer courseId = new Integer(0);
			Integer assignmentId = new Integer(0);
			Case value = new Case(null, courseId, assignmentId, Boolean.FALSE, title, title, numberOfDataPoints, "postgres");
			value.setNumberOfDataPoints(numberOfDataPoints);
			Integer id = new PostgresUtils().populateRecCase(connection, value);
			log.info(value.toString() + "Case row inserted to rec_case, caseId=" + id.toString());
			return id;
		} catch (Exception e) {
			throw e;
		}
	}

	private boolean populateCaseData(Connection connection, Integer caseId, List<Map<String, String>> jsonList, List<String> headerNames, String pvalueHeaderName) throws Exception {
		try {
			PostgresUtils service = new PostgresUtils();

			// table = rec_data_template
			String recTemplateTableName = ConfigUtils.getProperty("table.name.rec.data.template");
			List<String> recTemplateColumnNames = DdlUtils.getTableColumns(connection, recTemplateTableName);

			// table = rec_data_values
			String recValueTableName = ConfigUtils.getProperty("table.name.rec.data.values");
			List<String> recDataColumnNames = DdlUtils.getTableColumns(connection, recValueTableName);

			List<Case> lookupRecCaseList = new PostgresUtils().queryRecCase(connection, caseId);
			if (lookupRecCaseList == null || lookupRecCaseList.size() != 1) {
				throw new Exception("Cannot get case record for caseId=" + caseId.toString());
			}

			// template data
			int counter = 1;
			for (String columnName : headerNames) {
				boolean isPvalue = columnName.trim().toUpperCase().compareTo(pvalueHeaderName) == 0 ? true : false;

				// rec_data_template
				Integer columnNumber = new Integer(counter++);
				TemplateColumn data = new TemplateColumn(null, caseId, columnNumber, columnName, isPvalue);
				Integer id = service.populateRecTemplate(connection, recTemplateTableName, recTemplateColumnNames, data);
				data.setId(id);

				// rec_data_values
				for (Map<String, String> dataValues : jsonList) {
					String dataValue = dataValues.get(columnName);
					log.info(columnName + " -> " + dataValue);
					TemplateValue value = new TemplateValue(null, data.getId(), dataValue, NumberUtils.assignDataType(dataValue), isPvalue);
					id = service.populateRecValue(connection, recValueTableName, recDataColumnNames, value);
					if (id == null) {
						return false; // problem
					}
					// value.setId(id); no need here
				}

			}

		} catch (Exception e) {
			throw e;
		}
		return true;
	}

}
