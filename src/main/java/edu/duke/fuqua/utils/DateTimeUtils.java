package edu.duke.fuqua.utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.apache.log4j.Logger;

public class DateTimeUtils {
	private static Logger log = Logger.getLogger(DateTimeUtils.class);

	public static Timestamp incrementHours(Timestamp in, long hours, long minutes) throws Exception {
		try {
			LocalDateTime ldt = LocalDateTime.ofInstant(in.toInstant(), ZoneOffset.ofHours(-4));
			// log.info("ldt = " + ldt.toString());

			ldt = ldt.plusHours(hours).plusMinutes(minutes);
			// log.info("updated ldt = " + ldt);

			Timestamp retValue = Timestamp.valueOf(ldt);
			return retValue;
		} catch (Exception e) {
			throw e;
		}
	}

	public static int getDifferenceInMinutes(Timestamp older, Timestamp newer) throws Exception {

		try {
			long diff = Math.abs(newer.getTime() - older.getTime());
			long minutes = (diff / (1000 * 60)) % 60;
			return Long.valueOf(minutes).intValue();
		} catch (Exception e) {
			throw e;
		}

	}
}
