package edu.duke.fuqua.service;

import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;

import edu.duke.fuqua.utils.TestingUtils;
import edu.duke.fuqua.vo.CaseReport;
import edu.duke.fuqua.vo.thin.CaseReportLineItem;
import edu.duke.fuqua.vo.thin.CaseReportThin;
import edu.duke.fuqua.vo.thin.CaseThin;

public class CaseReportService {
	private static Logger log = Logger.getLogger(TestingUtils.class);

	private static CaseReportService instance;

	public static CaseReportService getInstance() {
		if (instance == null) {
			instance = new CaseReportService();
		}
		return instance;
	}

	public List<CaseReportThin> getAllCasesThin() throws Exception {
		try {
			return CaseLoader.getCasesThin();
		} catch (Exception e) {
			throw e;
		}
	}

	public CaseReport getThickCaseReport(Integer caseId) throws Exception {
		try {
			Optional<CaseReport> opt = CaseLoader.getCases().stream().filter(f -> f.getCaseRec().getId().compareTo(caseId) == 0).findFirst();
			if (opt.isPresent()) {
				return opt.get();
			} else {
				return null;
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public CaseReportThin getCaseForEdit(Integer caseId) throws Exception {
		try {
			return CaseLoader.getCaseForEdit(caseId);
		} catch (Exception e) {
			throw e;
		}
	}

	public CaseReportThin updateEditedCaseTitle(Integer caseId, String editedTitle) throws Exception {
		try {
			CaseReportThin retValue = CaseLoader.editedCaseCacheUpdate(caseId, editedTitle);
			if (retValue == null) {
				throw new Exception("Cannot locate case data for caseId = " + String.valueOf(caseId));
			}
			return retValue;
		} catch (Exception e) {
			throw e;
		}
	}

	public List<CaseReportLineItem> getCaseLineItems(Integer caseId) throws Exception {
		try {
			return CaseLoader.getCaseLineItems(caseId);
		} catch (Exception e) {
			throw e;
		}
	}

	public CaseThin getCaseThin(Integer caseId) throws Exception {
		try {
			return CaseLoader.getCaseThin(caseId);
		} catch (Exception e) {
			throw e;
		}
	}

//	public void insertToCache(Integer caseId) throws Exception {
//		try {
//			// CaseLoader.cacheInsert(caseId);
//			CaseLoader.loadAllDataIntoMemory();
//		} catch (Exception e) {
//			throw e;
//		}
//	}

	public void reload() throws Exception {
		try {
			CaseLoader.loadAllDataIntoMemory();
		} catch (Exception e) {
			throw e;
		}
	}

//	public void deleteFromCache(Integer caseId) throws Exception {
//		try {
//			// CaseReport deleteMe = getThickCaseReport(caseId);
//			// CaseLoader.cacheDelete(deleteMe);
//			CaseLoader.loadAllDataIntoMemory();
//		} catch (Exception e) {
//			throw e;
//		}
//	}
}
