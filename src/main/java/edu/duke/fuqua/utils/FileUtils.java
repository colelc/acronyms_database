package edu.duke.fuqua.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class FileUtils {

	private static Logger log = Logger.getLogger(FileUtils.class);

	public static String readInputStream(InputStream fileInputStream, String fileName, String fileType) throws Exception {
		try {
			String content = new BufferedReader(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8))/**/
					.lines()/**/
					.collect(Collectors.joining("\n"));

			log.info(content);
			return content;
		} catch (Exception e) {
			throw e;
		}
	}

	public static Set<String> getDirectoriesInDirectory(String directory, String targetFileName) throws Exception {
		try {
			return Stream.of(new File(directory).listFiles())/**/
					.filter(f -> f.getName().startsWith(targetFileName) && f.isDirectory())/**/
					.map(File::getName)/**/
					.collect(Collectors.toSet());
		} catch (Exception e) {
			throw e;
		}
	}

	public static Set<String> getFileMembersFromDirectory(String directory, String targetFileName) throws Exception {
		try {
			Set<String> test = Stream.of(new File(directory).listFiles())/**/
					.filter(f -> f.getName().startsWith(targetFileName) && f.isFile())/**/
					.map(File::getName)/**/
					.collect(Collectors.toSet());

			return test;
		} catch (Exception e) {
			throw e;
		}
	}

	public static String readFileAsString(String fileName) throws Exception {
		try {
			List<String> lines = readFileAsList(fileName);
			return lines == null || lines.size() == 0 ? "" : lines.stream().collect(Collectors.joining());
		} catch (Exception e) {
			throw e;
		}
	}

	public static List<String> readFileAsList(String fileName) throws Exception {
		try {
			return Files.readAllLines(Paths.get(fileName));
		} catch (Exception e) {
			throw e;
		}
	}

	public static List<String> getDenverColumns(String jsonFile) throws Exception {
		// these columns are sorted in same sequence as they appear in the original csv
		try {
			JsonArray jsonArray = JsonUtils.ingestJsonFileAsJsonArray(Paths.get(jsonFile));
			return jsonArray.get(0).getAsJsonObject().keySet().stream().collect(Collectors.toList());
		} catch (Exception e) {
			throw e;
		}
	}

	public static List<Map<String, Object>> getCaseDataFromJsonFile(Integer caseId, String jsonFile) {

		List<Map<String, Object>> rawJson = new ArrayList<>();

		try {
			JsonArray jsonArray = JsonUtils.ingestJsonFileAsJsonArray(Paths.get(jsonFile));

			// get columns list
			List<String> columns = jsonArray.get(0).getAsJsonObject().keySet().stream().collect(Collectors.toList());

			Map<String, JsonObject> structure = new HashMap<>();
			for (String columnName : columns) {
				log.info(columnName);
				JsonObject jsonObject = new JsonObject();

				jsonObject.addProperty("title", columnName);

				JsonArray columnValues = new JsonArray();

				for (JsonElement el : jsonArray) {
					String value = el.getAsJsonObject().get(columnName).getAsString();
					JsonObject columnObject = new JsonObject();
					columnObject.addProperty("value", value);
					columnValues.add(columnObject);
				}

				jsonObject.add("values", columnValues);
				structure.put(columnName, jsonObject);
			}

			// spin through the structure
			for (String columnName : structure.keySet()) {
				Map<String, Object> map = new HashMap<>();
				map.put("id", caseId);
				map.put("columns", structure.get(columnName).get("values").getAsJsonArray());
				map.put("input_column_title", ""); // I don't think this is used
				map.put("title", columnName);
				rawJson.add(map);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return rawJson;
	}

	public static List<Map<String, Object>> getHistoricalCaseFiles() {

		List<Map<String, Object>> rawJson = new ArrayList<>();

		try {
			Set<String> caseFiles = new HashSet<>();
			String historicalDirectory = ConfigUtils.getProperty("history.directory");

			Set<String> dirs = FileUtils.getDirectoriesInDirectory(historicalDirectory, "");
			dirs.forEach(directory -> {
				try {
					Set<String> files = FileUtils.getFileMembersFromDirectory(historicalDirectory + File.separator + directory, "data.json");
					files.forEach(f -> caseFiles.add(historicalDirectory + File.separator + directory + File.separator + f));
				} catch (Exception e) {
					log.error(e.getMessage());
					e.printStackTrace();
					System.exit(99);
				}
			});

			caseFiles.forEach(caseFile -> {
				try {
					JsonObject jsonObject = JsonUtils.ingestJsonFileAsJsonObject(Paths.get(caseFile));
					Map<String, Object> map = new HashMap<>();
					map.put("id", jsonObject.getAsJsonPrimitive("id"));
					map.put("columns", jsonObject.getAsJsonArray("columns"));
					map.put("input_column_title", jsonObject.get("input_column_title"));
					map.put("owner", jsonObject.get("owner"));
					map.put("p_values", jsonObject.getAsJsonObject("p_values"));
					rawJson.add(map);
				} catch (Exception e) {
					log.error(e.getMessage());
					e.printStackTrace();
					System.exit(99);
				}
			});
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return rawJson;
	}

	public static List<Map<String, Object>> getHistoricalStudentDataFiles() {

		List<Map<String, Object>> rawJson = new ArrayList<>();

		try {
			Set<String> studentFiles = new HashSet<>();
			String historicalDirectory = ConfigUtils.getProperty("history.directory");

			Set<String> dirs = FileUtils.getDirectoriesInDirectory(historicalDirectory, "");
			for (String dir : dirs) {
				// log.info(dir);
				if (Files.notExists(Paths.get(historicalDirectory + File.separator + dir + File.separator + "responses"))) {
					log.info(dir + " -> " + "No student responses found - we will skip");
					continue;
				}
				Set<String> files = FileUtils.getFileMembersFromDirectory(historicalDirectory + File.separator + dir + File.separator + "responses", "");
				for (String file : files) {
					studentFiles.add(historicalDirectory + File.separator + dir + File.separator + "responses" + File.separator + file);
				}
			}

			studentFiles.forEach(studentFile -> {
				try {
					JsonObject jsonObject = JsonUtils.ingestJsonFileAsJsonObject(Paths.get(studentFile));
					Map<String, Object> map = new HashMap<>();
					map.put("id", jsonObject.getAsJsonPrimitive("id"));

					map.put("uid", jsonObject.getAsJsonPrimitive("uid").getAsString());

					Object first = jsonObject.get("first");
					map.put("firstName", (first instanceof JsonNull ? null : ((JsonPrimitive) first).getAsString()));

					Object last = jsonObject.get("last");
					map.put("lastName", (last instanceof JsonNull ? null : ((JsonPrimitive) last).getAsString()));

					Object preferredFirst = jsonObject.get("preferred_first");
					map.put("preferredFirstName", (preferredFirst instanceof JsonNull ? null : ((JsonPrimitive) preferredFirst).getAsString()));

					Object preferredLast = jsonObject.get("preferred_last");
					map.put("preferredLastName", (preferredLast instanceof JsonNull ? null : ((JsonPrimitive) preferredLast).getAsString()));

					Object score = jsonObject.get("score");
					map.put("score", (score instanceof JsonNull ? null : ((JsonPrimitive) score).getAsFloat()));

					JsonObject input = ((JsonElement) jsonObject.get("input")).getAsJsonObject();
					JsonArray valuesArray = input.getAsJsonArray("values");
					List<Float> floats = new ArrayList<>();
					for (JsonElement value : valuesArray) {
						String number = value.getAsJsonObject().get("value").getAsString();
						Float computed = NumberUtils.convertToFloat(number);
						if (computed != null) {
							floats.add(NumberUtils.convertToFloat(number));
						} else {
							log.info(studentFile + " -> cannot use value = " + value.toString());
						}
					}
					map.put("values", floats);

					rawJson.add(map);
				} catch (Exception e) {
					log.error(e.getMessage());
					e.printStackTrace();
					System.exit(99);
				}
			});
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return rawJson;
	}

}
