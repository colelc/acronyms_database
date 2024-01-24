package edu.duke.fuqua.utils;

import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ConfigUtils {
	private static Logger log = Logger.getLogger(ConfigUtils.class);

	private static String ENV; // no getter/setter by design
	private static Properties config = new Properties();

	static {
		try {
			log.info("Loading configuration....");
			loadProperties();
			ENV = getENV();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			// System.exit(99);
		}
	}

	public static void loadProperties() throws Exception {

		log.info("These are the configuration key-value pairs");
		try (InputStream in = ConfigUtils.class.getResourceAsStream("/app.properties");) {
			config.load(in);
			config.forEach((k, v) -> {
				String key = (String) k;
				log.info("     " + key + " -> " + (key.toLowerCase().contains("passw") ? "xxxxxxx" : v));
			});
		} catch (Exception e) {
			throw e;
		}
		log.info("End of configuration key-value pairs");
	}

	/**
	 * @return the config
	 */
	public static Properties getConfig() {
		return config;
	}

	public static String getProperty(String key) throws Exception {
		String value = null;
		try {
			if (key == null || key.trim().length() == 0) {
				throw new Exception("Unknown property key");
			}

			String editedKey = (ENV == null) ? key : ENV + "." + key;
			value = config.getProperty(editedKey);
			if (value == null) {
				// log.warn("Key Value (" + key + ") is not prefixed with ENV value - this may
				// or may not be ok");
				value = config.getProperty(key);
				// log.warn(key + " lookup value is " + value);
				if (value == null) {
					throw new Exception("Unknown property value for key: " + ENV + "." + key);
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return value;
	}

	public static int getPropertyAsInt(String key) throws Exception {
		try {
			String value = getProperty(key);
			return Integer.valueOf(value).intValue();
		} catch (Exception e) {
			throw e;
		}
	}

	public static boolean getPropertyAsBoolean(String key) throws Exception {
		try {
			String value = getProperty(key);
			if (value == null || value.trim().length() == 0 || value.substring(0, 1).toLowerCase().compareTo("y") != 0) {
				return false;
			}
			return true;
		} catch (Exception e) {
			throw e;
		}
	}

	public static String getENV() throws Exception {
		try {
			return config.getProperty("ENV");
		} catch (Exception e) {
			throw e;
		}
	}

}
