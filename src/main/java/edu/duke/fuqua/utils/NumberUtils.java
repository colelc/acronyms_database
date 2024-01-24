package edu.duke.fuqua.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

public class NumberUtils {

	private static String dataTypeInteger;
	private static String dataTypeFloat;
	private static String dataTypeString;
	private static String dataTypeNone;
	private static List<String> dataNotAvailable;

	private static String[] numbers = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
	private static List<String> numbersList = Arrays.asList(numbers);
	private static Logger log = Logger.getLogger(NumberUtils.class);

	static {
		try {
			dataTypeInteger = ConfigUtils.getProperty("data.type.integer");
			dataTypeFloat = ConfigUtils.getProperty("data.type.float");
			dataTypeString = ConfigUtils.getProperty("data.type.string");
			dataTypeNone = ConfigUtils.getProperty("data.type.none");
			dataNotAvailable = Arrays.asList(ConfigUtils.getProperty("data.is.na").split(",")).stream().map(m -> m.toUpperCase().trim()).collect(Collectors.toList());
		} catch (Exception e) {
			dataTypeInteger = "integer";
			dataTypeFloat = "float";
			dataTypeString = "string";
			dataTypeNone = "none";
			dataNotAvailable = new ArrayList<>();
			dataNotAvailable.add("na");
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public static String assignDataType(String strNum) throws Exception {
		if (strNum == null) {
			return dataTypeString;
		}

		try {
			Integer.parseInt(strNum);
			return dataTypeInteger;
		} catch (NumberFormatException nfe) {
			try {
				Float.parseFloat(strNum);
				return dataTypeFloat;
			} catch (NumberFormatException nfe2) {
				if (dataNotAvailable.contains(strNum.toUpperCase().trim())) {
					return dataTypeNone;
				}
				return dataTypeString;
			}
		}
	}

	public static Float convertToFloat(String in) throws Exception {
		try {
			String dataType = assignDataType(in);

			// try to strip non-numeric characters
			if (dataType.compareTo(dataTypeInteger) == 0) {
				Float f = Float.valueOf(String.valueOf(in));
				return f;
			}

			if (dataType.compareTo(dataTypeString) == 0) {
				try {
					Float f = Float.valueOf(in);
					return f;
				} catch (NumberFormatException nfe) {
					// log.error(in + " -> cannot convert to float without manipulation");
					String stripped = stripNonNumeric(in);
					try {
						return Float.valueOf(stripped);
					} catch (NumberFormatException e) {
						// log.info("Cannot format this value into a float: " + in);
						return null;
					}
				}
			}

			if (dataType.compareTo(dataTypeFloat) == 0) {
				Float f = Float.valueOf(in);
				return f;
			}

			throw new Exception("I dont know what this is");
		} catch (Exception e) {
			throw e;
		}
	}

	public static String stripNonNumeric(String in) throws Exception {
		String out = "";

		try {
			if (in == null) {
				return null;
			}

			if (in.trim().length() == 0) {
				return "";
			}

			for (int i = 0; i < in.length(); i++) {
				String character = in.substring(i, i + 1);
				if (numbersList.contains(character)) {
					out += character;
				}
			}
		} catch (Exception e) {
			throw e;
		}

		return out;
	}

	public static String getDataTypeInteger() {
		return dataTypeInteger;
	}

	public static String getDataTypeFloat() {
		return dataTypeFloat;
	}

	public static String getDataTypeString() {
		return dataTypeString;
	}

	public static String getDataTypeNone() {
		return dataTypeNone;
	}

	public static List<String> getDataNotAvailable() {
		return dataNotAvailable;
	}

}
