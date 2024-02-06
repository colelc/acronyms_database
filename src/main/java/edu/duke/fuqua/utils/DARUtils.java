package edu.duke.fuqua.utils;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import edu.duke.fuqua.db.ConnectionService;
import edu.duke.fuqua.db.CreateService;
import edu.duke.fuqua.vo.BoardMember;
import edu.duke.fuqua.vo.ExcelAcronym;
import edu.duke.fuqua.vo.Tag;

public class DARUtils {

	private static Logger log = Logger.getLogger(DARUtils.class);

	public static void loadDARBoardMembers() {
		try {
			String darDirectory = ConfigUtils.getProperty("dar.excel.directory");
			String csvFile = darDirectory + File.separator + ConfigUtils.getProperty("dar.excel.csv.file.board.members");
			List<Map<String, String>> csvList = ExcelUtils.csvToList(csvFile);

			populateDARBoardMembers(csvList);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private static void populateDARBoardMembers(List<Map<String, String>> csvList) throws Exception {
		try {
			Connection connection = ConnectionService.connect("postgres");
			PostgresUtils service = new PostgresUtils();

			String table = ConfigUtils.getProperty("table.name.dar.board.members");
			List<String> columnNames = DdlUtils.getTableColumns(connection, table);
//			for (String c : columnNames) {
//				System.out.println(c);
//			}

			// template data
			for (Map<String, String> map : csvList) {
				log.info(map.toString());
				BoardMember bm = new BoardMember();
				bm.setEntityId(map.get("ENTITY_ID"));
				bm.setBoardFname(map.get("First_Name"));
				bm.setBoardLname(map.get("Last_Name"));
				bm.setBoardPreferredName("");
				bm.setBoardEmail("");
				bm.setBoardClass(map.get("Fuqua_Class_Yr"));
				bm.setProgram(map.get("Fuqua_Program"));
				bm.setHsmCert(map.get("HSM_Cert"));
				bm.setOtherDukeDegree(map.get("Other_Duke_Deg"));
				bm.setEmployer(map.get("Employer"));
				bm.setJobTitle(map.get("Job_Title"));
				bm.setLinkedIn(map.get("LINKEDIN"));
				bm.setCurServeOn(map.get("Currently_Serving_On"));
				bm.setBoardPhoto(map.get("JPG FILE NAME"));
				bm.setCreatedBy("postgres");
				bm.setLastUpdatedBy("postgres");

				Integer id = service.populateDARBoardMembers(connection, table, columnNames, bm);
			}

		} catch (

		Exception e) {
			throw e;
		}
	}

	public static void loadDARAvailableAppts() {
		try {
			String darDirectory = ConfigUtils.getProperty("dar.excel.directory");
			String csvFile = darDirectory + File.separator + ConfigUtils.getProperty("dar.excel.csv.file.available.appts");
			List<Map<String, String>> csvList = ExcelUtils.csvToList(csvFile);

			populateDARAvailableAppts(csvList);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private static void populateDARAvailableAppts(List<Map<String, String>> csvList) throws Exception {
		try {
			Connection connection = ConnectionService.connect("postgres");
			PostgresUtils service = new PostgresUtils();

			String table = ConfigUtils.getProperty("table.name.dar.available.appts");
			List<String> columnNames = DdlUtils.getTableColumnsDARAvailableAppts(connection, table);
			System.out.println(columnNames.stream().collect(Collectors.joining(", ")));

			for (Map<String, String> map : csvList) {
				log.info(map.toString());

				Integer boardId = service.queryDARBoardMembersByEntityId(connection, map.get("ENTITY_ID"));

				String apptDateText = map.get("DATE");

				// DATE_DB = appt_date
				String apptDateString = map.get("DATE_DB");
				Timestamp apptDate = Timestamp.valueOf(apptDateString);

				String startTimeText = map.get("START_TIME");
				String startTimeString = map.get("START_TIME_DB");
				long startHours = getHours(startTimeString);
				long startMinutes = getMinutes(startTimeString);
				Timestamp apptStartTime = DateTimeUtils.incrementHours(apptDate, startHours, startMinutes);

				String endTimeText = map.get("END_TIME");
				String endTimeString = map.get("END_TIME_DB");
				long endHours = getHours(endTimeString);
				long endMinutes = getMinutes(endTimeString);
				Timestamp apptEndTime = DateTimeUtils.incrementHours(apptDate, endHours, endMinutes);

				Integer apptDuration = DateTimeUtils.getDifferenceInMinutes(apptEndTime, apptStartTime);

				// appt_count - set to 0 for now - ask fred
				Integer aptCount = new Integer(0);

				String createdTimestampString = map.get("WHEN_ENTERED_DB");
				Timestamp createdTimestamp = Timestamp.valueOf(createdTimestampString);

				String sql = "INSERT INTO " + PostgresUtils.getDbName() + "." + "dar_available_appts" + " "/**/
						+ " (" + columnNames.stream().collect(Collectors.joining(", ")) + " ) " /**/
						+ " VALUES " /**/
						+ " (" + columnNames.stream().map(m -> "?").collect(Collectors.joining(", ")) + " ) " /**/
						+ " RETURNING id "/**/
						+ "; ";

				log.info(sql);
				CreateService createService = new CreateService();

				PreparedStatement ps = connection.prepareStatement(sql);

				ps.setInt(1, boardId);
				ps.setString(2, apptDateText);
				ps.setTimestamp(3, apptDate);
				ps.setString(4, startTimeText);
				ps.setTimestamp(5, apptStartTime);
				ps.setString(6, endTimeText);
				ps.setTimestamp(7, apptEndTime);
				ps.setInt(8, apptDuration);
				ps.setInt(9, aptCount);
				ps.setString(10, "New"); // appt_status
				ps.setBoolean(11, true); // appt_active
				ps.setString(12, "postgres"); // created_by
				ps.setTimestamp(13, createdTimestamp);

				Integer id = createService.insert(connection, ps);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	private static long getHours(String in) throws Exception {
		String[] tokens = in.split(":");
		if (tokens == null || tokens.length != 2) {
			throw new Exception("ill-formatted time = " + in);
		}

		String hh = tokens[0];
		long hours = Long.valueOf(hh).longValue();
		// log.info("hours=" + String.valueOf(hours));
		return hours;
	}

	private static long getMinutes(String in) throws Exception {
		String[] tokens = in.split(":");
		if (tokens == null || tokens.length != 2) {
			throw new Exception("ill-formatted time = " + in);
		}

		String mm = tokens[1];
		long minutes = Long.valueOf(mm).longValue();
		// log.info("minutes=" + String.valueOf(minutes));
		return minutes;
	}

	private static void populateFuquaAcronymTags(List<String> tagList) throws Exception {
		try {
			Connection connection = ConnectionService.connect("postgres");
			PostgresUtils service = new PostgresUtils();

			// table = fuqua_acronyms
			String table = ConfigUtils.getProperty("table.name.fuqua.acronym.tags");
			List<String> columnNames = DdlUtils.getTableColumns(connection, table);

			// tags
			for (String tag : tagList) {
				Integer id = service.populateFuquaAcronymTag(connection, table, columnNames, tag.toUpperCase().trim());
			}

		} catch (Exception e) {
			throw e;
		}
	}

	public static void loadAcronymTags() {
		try {
			Connection connection = ConnectionService.connect("postgres");
			PostgresUtils service = new PostgresUtils();
			List<ExcelAcronym> list = service.queryFuquaAcronyms(connection, null);
			if (list == null || list.size() == 0) {
				throw new Exception("No data in fuqua_acronyms table - cannot populate fuqua_acronym_tags table");
			}

			List<String> tagList = list.stream().map(m -> m.getAreaKey()).distinct().collect(Collectors.toList());

			// hack for multiple tags in same Excel cell
			List<String> copy = new ArrayList<>();
			List<String> splitterList = new ArrayList<>();

			for (int i = 0; i < tagList.size(); i++) {
				String t = tagList.get(i);
				String splitter = "";
				if (t.contains("\r\n")) {
					splitter = "\r\n";
				} else if (t.contains("\r")) {
					splitter = "\r";
				} else if (t.contains("\n")) {
					splitter = "\n";
				} else {
					splitter = null;
				}

				if (splitter != null) {
					splitterList.clear();
					splitterList.addAll(Arrays.asList(t.split(splitter)));

					for (String s : splitterList) {
						copy.add(s);
						log.info("Added: " + s);
					}
				} else {
					copy.add(t);
					log.info("Added: " + t);
				}
			}

			populateFuquaAcronymTags(copy);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public static void loadAcronymTagMap() {
		try {
			Connection connection = ConnectionService.connect("postgres");
			PostgresUtils service = new PostgresUtils();

			List<ExcelAcronym> list = service.queryFuquaAcronyms(connection, null);
			if (list == null || list.size() == 0) {
				throw new Exception("No data in fuqua_acronyms table - cannot populate fuqua_acronym_tags table");
			}

			List<Tag> tagList = service.queryFuquaAcronymTags(connection, null);
			if (tagList == null || tagList.size() == 0) {
				throw new Exception("No data in fuqua_acronym_tags table - cannot populate fuqua_acronym_tag_map table");
			}

			String table = ConfigUtils.getProperty("table.name.fuqua.acronym.tag.map");
			List<String> columnNames = DdlUtils.getTableColumns(connection, table);

			for (ExcelAcronym a : list) {
				// if (a.getAcronym().trim().toLowerCase().compareTo("wic") == 0) {
				// continue;
				// }
				Integer tagId = tagList.stream()/**/
						.filter(f -> f.getName().trim().toLowerCase().compareTo(a.getAreaKey().trim().toLowerCase()) == 0)/**/
						.map(m -> m.getId())/**/
						.findFirst().get();

				service.populateFuquaAcronymTagMap(connection, table, columnNames, a.getId(), tagId);
			}

			// now take care of WIC
//			Integer acronymId = list.stream().filter(f -> f.getAcronym().compareTo("WIC") == 0).map(m -> m.getId()).findFirst().get();
//			Integer hsmTagId = tagList.stream().filter(f -> f.getName().compareTo("HSM") == 0).map(m -> m.getId()).findFirst().get();
//			Integer mbaTagId = tagList.stream().filter(f -> f.getName().compareTo("MBA") == 0).map(m -> m.getId()).findFirst().get();
//			Integer mmsTagId = tagList.stream().filter(f -> f.getName().compareTo("MMS") == 0).map(m -> m.getId()).findFirst().get();
//
//			service.populateFuquaAcronymTagMap(connection, table, columnNames, acronymId, hsmTagId);
//			service.populateFuquaAcronymTagMap(connection, table, columnNames, acronymId, mbaTagId);
//			service.populateFuquaAcronymTagMap(connection, table, columnNames, acronymId, mmsTagId);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public static void updateTagString() {
		try {
			// String acronymDirectory = ConfigUtils.getProperty("acronym.excel.directory");
			// String csvFile = acronymDirectory + File.separator + ConfigUtils.getProperty("acronym.excel.csv.file");
			// List<Map<String, String>> csvList = ExcelUtils.csvToList(csvFile);

			// populateFuquaAcronyms(csvList);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

}
