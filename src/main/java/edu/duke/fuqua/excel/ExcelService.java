package edu.duke.fuqua.excel;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import edu.duke.fuqua.utils.ConfigUtils;
import edu.duke.fuqua.utils.ExcelUtils;
import edu.duke.fuqua.vo.ExcelAcronym;

public class ExcelService {
	private String excelTempDirectory;
	private static Logger log = Logger.getLogger(ExcelService.class);

	public void writeDataToExcel(List<ExcelAcronym> lineItems) throws Exception {
		try {
			for (ExcelAcronym li : lineItems) {
				caseToExcel(li);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public void caseToExcel(ExcelAcronym report) throws Exception {
		// String fileName = getExcelTempDirectory() + File.separator + report.getCaseRec().getTitle()/**/
		// .replace(" ", "_").replace("(", "").replace(")", "").replace(":", "").replace("-", "") /**/
		// + ".xlsx";
		String fileName = "";
		log.info(fileName);

		try (FileOutputStream out = new FileOutputStream(fileName)) {
			// Workbook workbook = new HSSFWorkbook();
			// Workbook workbook = WorkbookFactory.create(new File(fileName));
			Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet();

			// List<String> columnNames = new ArrayList<>(getColumnNames(new Meta()));
			// for (int i = 0; i < numDataPoints.intValue(); i++) {
			// columnNames.add("Value " + String.valueOf(columnIx++));
			// }
			List<String> columnNames = new ArrayList<>();

			ExcelUtils.createColumnHeaders(workbook, sheet, columnNames);

			int rowNumber = 1;

			// pValues
			ExcelUtils.createDataRow(workbook, sheet, rowNumber++/* , report.getpValues() */);

			workbook.write(out);
			out.close();
		} catch (Exception e) {
			throw e;
		}
	}

	public String getExcelTempDirectory() throws Exception {
		if (excelTempDirectory == null) {
			try {
				excelTempDirectory = ConfigUtils.getProperty("excel.temp.directory");
			} catch (Exception e) {
				throw e;
			}
		}
		return excelTempDirectory;
	}

}
