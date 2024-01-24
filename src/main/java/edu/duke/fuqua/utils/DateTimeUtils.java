package edu.duke.fuqua.utils;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

import org.apache.log4j.Logger;

public class DateTimeUtils {

	private static Logger log = Logger.getLogger(DateTimeUtils.class);

	public static String timestampToString(Timestamp ts) throws Exception {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
			String retValue = formatter.format(ts.toLocalDateTime());
			return retValue;
		} catch (Exception e) {
			throw e;
		}
	}

}
