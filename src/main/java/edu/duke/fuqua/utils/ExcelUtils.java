package edu.duke.fuqua.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

public class ExcelUtils {

	private static Logger log = Logger.getLogger(ExcelUtils.class);

	public static List<Map<String, String>> csvToList(String csvFile) throws Exception {

		try (InputStream in = new FileInputStream(csvFile);) {
			CSV csv = new CSV(true, ',', in);
			List<String> fieldNames = null;

			if (csv.hasNext()) {
				fieldNames = new ArrayList<>(csv.next());
			}

			List<Map<String, String>> list = new ArrayList<>();
			while (csv.hasNext()) {
				List<String> x = csv.next();
				Map<String, String> obj = new LinkedHashMap<>();
				for (int i = 0; i < fieldNames.size(); i++) {
					obj.put(fieldNames.get(i), x.get(i));
				}
				list.add(obj);
			}
			return list;
		}
	}

	public static CellStyle getCellStyle(Workbook wb, boolean bolded, boolean italic, HorizontalAlignment horizontal, VerticalAlignment vertical) {
		CellStyle cellStyle = wb.createCellStyle();

		Font font = wb.createFont();
		font.setBold(bolded);
		font.setItalic(italic);
		// font.setColor((short) 0xc); // blue
		font.setFontHeightInPoints((short) 9);

		cellStyle.setFont(font);

		cellStyle.setAlignment(horizontal);
		cellStyle.setVerticalAlignment(vertical);

		return cellStyle;
	}

}
