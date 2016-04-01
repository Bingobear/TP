package modules;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.Normalizer;
import java.util.ArrayList;

/**
 * Created by simonbruns on 16/03/16.
 */
public class TitleMatcher {
    private ArrayList<String> titles;

    /**
     * Maps the file names with the reference titles (csv). Work around for pdfs
     * that have no corresponding publication within the library information
     *
     * @param fileName (fileEntry.getName())
     * @return
     */

    public String getTitle(String fileName) {
        for (int ii = 0; ii < titles.size(); ii = ii + 2) {
            if (titles.get(ii).contains(fileName)) {
                String titleNorm = Normalizer.normalize(titles.get(ii + 1),
                        Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
                return titleNorm;
            }
        }
        return fileName;

    }

    /*
     * TODO Don't implement logic in setters!
     */
    public void initializeKnownTitles(String location) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        String importtitle = classLoader.getResource(location + "/titles/pdftitleo.csv").getFile();
        titles = readCSVTitle(importtitle);
    }

    /**
     * retrieve titles from external csv title file. External mapping exists for
     * unknown pdfs (titles) to the library file.
     *
     * @param csvFile
     * @return
     * @throws IOException
     */
    ArrayList<String> readCSVTitle(String csvFile) throws IOException {
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        ArrayList<String> titles = new ArrayList<String>();
        String[] titleEntries = null;
        Reader in = new FileReader(csvFile);
        Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
        for (CSVRecord record : records) {
            line = record.get(0);
            titleEntries = line.split(cvsSplitBy);
            for (String currentTitle : titleEntries) {
                titles.add(currentTitle);
            }
        }
        return titles;
    }

}
