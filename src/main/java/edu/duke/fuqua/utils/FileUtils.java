package edu.duke.fuqua.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

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

}
