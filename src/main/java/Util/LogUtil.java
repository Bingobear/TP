package Util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import models.Category;
import models.WordOcc;

public class LogUtil {
	/** Writes a keyword log to protocol what keywords were extracted for which pdf
	 * @param keywords2
	 * @param name
	 * @param seperator
	 * @param size
	 * @return 
	 * @throws IOException
	 */
	public static void writelog(ArrayList<Category> keywords2, String name,
			String seperator, int size,String language) throws IOException {
		String timeLog = "Keywords_log_new";
		File logFile = new File(timeLog);

		// This will output the full path where the file will be written to...
		System.out.println(logFile.getCanonicalPath());

		BufferedWriter writer;
		writer = new BufferedWriter(new FileWriter(logFile, true));
		writer.write("Name: " + name + ", seperaotr: " + seperator
				+ ", Stringextract: " + size + ", lang:" + language);
		writer.newLine();
		for (int ii = 0; ii < keywords2.size(); ii++) {
			writer.write(keywords2.get(ii).getTitle() + " + "
					+ keywords2.get(ii).getNormtitle() + ", ");
			writer.newLine();
		}
		writer.write("_________________________________________________________");
		writer.newLine();
		writer.close();

	}
	
	/***
	 * Gemerates a text file to evaluate the generated word array from the
	 * parsed pdf
	 * 
	 * @param keyOcc
	 * @param path
	 * @param title
	 */
	@SuppressWarnings("unused")
	private static void createTextExport(ArrayList<WordOcc> keyOcc,
			String path, String title) {
		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(path + title + ".txt"), "utf-8"));
			for (int ii = 0; ii < keyOcc.size(); ii++) {
				WordOcc current = keyOcc.get(ii);

				writer.write(current.getWord().getWord() + ";"
						+ current.getOcc() + ";");

			}
		} catch (IOException ex) {
			// report
		} finally {
			try {
				writer.close();
			} catch (Exception ex) {
			}
		}
	}
}
