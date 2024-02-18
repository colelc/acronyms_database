package edu.duke.fuqua.utils;

import java.sql.Connection;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import edu.duke.fuqua.db.ConnectionService;
import edu.duke.fuqua.db.ReadService;

public class QueryUtils {

	private static Logger log = Logger.getLogger(QueryUtils.class);

	public static void queryTests() throws Exception {
		try {
			Connection connection = ConnectionService.connect("postgres");
			// queryAcronymTags(connection, null);
			;
		} catch (Exception e) {
			throw e;
		}
	}

	public static void queryAcronymTags(Connection connection, Integer acronymId) throws Exception {
		log.info("ACRONYM TAGS");
		try {
			String sql = "SELECT "/**/
					+ " a.acronym, t.name " /**/
					+ " from " /**/
					+ " fuqua_acronyms a, fuqua_acronym_tags t, fuqua_acronym_tag_map m "/**/
					+ " where "/**/
			;

			if (acronymId != null) {
				sql += " a.id = ? and ";
			}

			sql += " a.id = m.acronym_id "/**/
					+ " and t.id = m.tag_id "/**/
					+ " order by a.acronym, t.name "/**/
			;

			log.info(sql);
			ResultSet rs = new ReadService().doQuery(connection, sql);

			while (rs.next()) {
				log.info(rs.getString("acronym") + " -> " + rs.getString("name"));
			}

		} catch (Exception e) {
			throw e;
		}
	}
}
