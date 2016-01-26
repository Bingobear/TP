package modules;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import models.Category;
import models.PDF;
import models.Words;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import Util.KeywordUtil;
import Util.LogUtil;
import Util.NLPUtil;

import com.cybozu.labs.langdetect.LangDetectException;

/**
 * Main Text Mining Class
 * 
 * @author Simon Bruns
 *
 */
public class PDFExtractor {

	/**
	 * PDF Extractor
	 * 
	 * @throws IOException
	 */
	private String titlePage;
	private int catnumb;
	private int pagenumber;
	private int endK = 0;
	private String language;
	private ArrayList<Category> keywords = new ArrayList<Category>();

	public ArrayList<Category> getKeywords() {
		return keywords;
	}

	public void setKeywords(ArrayList<Category> keywords) {
		this.keywords = keywords;
	}

	private int wordcount = 0;

	public int getWordcount() {
		return wordcount;
	}

	PDFExtractor() {

	}

	PDFExtractor(String lang) {
		this.language = lang;
	}

	public void setLang(String lang) {
		this.language = lang;
	}

	public String getLang() {
		return this.language;
	}

	public String getTitlePage() {
		return titlePage;
	}

	public void setTitlePage(String titlePage) {
		this.titlePage = titlePage;
	}

	/**Retrieve number of categories for this pdf
	 * @return
	 */
	public int getCatnumb() {
		return catnumb;
	}

	public void setCatnumb(int catnumb) {
		this.catnumb = catnumb;
	}

	public int getPagenumber() {
		return pagenumber;
	}

	public void setPagenumber(int pagenumber) {
		this.pagenumber = pagenumber;
	}




	/**
	 * Parses PDFfile -> performs textmining: keyword-extraction,
	 * word-extraction
	 * 
	 * @param fileEntry
	 * @param first
	 * @param arrayList
	 * @param url2
	 * 
	 * @return ArrayList of words
	 * @throws LangDetectException
	 * @throws IOException
	 */
	public ArrayList<Words> parsePDFtoKey(File fileEntry, boolean first,
			ArrayList<PDF> arrayList) throws LangDetectException, IOException {
		ArrayList<Words> result = new ArrayList<Words>();

		PDFTextStripper pdfStripper = null;
		PDDocument pdDoc = null;
		COSDocument cosDoc = null;
		setTitlePage(fileEntry.getName());

		PDFParser parser = new PDFParser(new FileInputStream(fileEntry));
		parser.parse();
		cosDoc = parser.getDocument();
		pdfStripper = new PDFTextStripper();

		pdDoc = new PDDocument(cosDoc);
		this.setPagenumber(pdDoc.getNumberOfPages());
		LangDetect lang = new LangDetect();

		for (int counter = 0; counter < pdDoc.getNumberOfPages(); counter += 5) {
			String parsedText = NLPUtil.parsePdftoString(pdfStripper, pdDoc, counter,
					counter + 4);
			if (!((counter == 0) && (parsedText.length() < 50))) {
				setLang(lang.detect(parsedText, first));

				if (counter == 0) {
					System.out.println(getLang());
					if (first) {
						first = false;
					}

					this.setTitlePage(NLPUtil.parsePdftoString(pdfStripper, pdDoc,
							counter, counter + 1));
					if (pdfExist(this.getTitlePage(), arrayList)) {
						break;
					}

					parsedText = parsedText.toLowerCase();
					String[] tokens = NLPUtil.getTokenPM(parsedText,this.language);
					ArrayList<Category> keywords = getKeywordsFromPDF(tokens,
							fileEntry.getName());
					if (keywords.isEmpty()) {
						break;
					} else if ((keywords.size() < 4) || (keywords.size() > 8)) {
						if (this.titlePage.length() > endK) {
							this.titlePage = this.titlePage.substring(0,
									endK - 1);
						}
						this.setKeywords(keywords);
					}

					else {
						if (this.titlePage.length() > endK) {
							this.titlePage = this.titlePage.substring(0,
									endK - 1);
						}
						this.setKeywords(keywords);
					}
				}

				parsedText = parsedText.toLowerCase();

				// sentence detector -> tokenizer
				String[] tokens = NLPUtil.getToken(parsedText,this.language);
				String[] filter = NLPUtil.posttags(tokens,this.language);
				ArrayList<Words> words = NLPUtil.generateWords(filter, tokens, 0,this.getLang(),this.getKeywords());
				result.addAll(words);
				System.out.println("normal:" + tokens.length
						+ ", optimiertNouns:" + words.size());
				System.out.println("");
				wordcount = wordcount + tokens.length;
			} else {
				System.out.println("Bad Paper or Presentation");
				break;
			}
		}
		cosDoc.close();
		System.out.println("FINAL RESULT:optimiertNouns:" + result.size());
		return result;
	}

	/**
	 * Test if PDF is complete and not fragmented after parsing it
	 * 
	 * @param titlepage
	 * @param pdfList
	 * @return
	 */
	private boolean pdfExist(String titlepage, ArrayList<PDF> pdfList) {
		int sublength = 20;
		for (PDF compare : pdfList) {
			if (titlepage.length() < 20) {
				sublength = titlepage.length() - 1;
			}
			if (compare.getFirstPage().length() < sublength) {
				sublength = compare.getFirstPage().length() - 1;
			}
			if (compare.getFirstPage().substring(0, sublength)
					.equals(titlepage.substring(0, sublength))) {
				System.out.println("WORKS");
				return true;
			}
		}
		return false;
	}

	/**Extracts keywords from a given pdf (token string[]=tokenpm)
	 * @param tokens
	 * @param name
	 * @return
	 */
	private ArrayList<Category> getKeywordsFromPDF(String[] tokens, String name) {
		ArrayList<Category> keywords = new ArrayList<Category>();
		ArrayList<String> textPDF = new ArrayList<String>(Arrays.asList(tokens));
		int start = KeywordUtil.findKeyWStart(textPDF);
		endK = start;
		String seperator = "";
		if (start > 0) {

			if (textPDF.get(start).equals(":")) {
				start++;
			}
			if (textPDF.get(start).contains("keywords")) {
				String value = textPDF.get(start);
				textPDF.set(start, value.replaceAll("keywords", ""));
			}
			if (textPDF.get(start).contains("terms")) {
				String value = textPDF.get(start);
				textPDF.set(start, value.replaceAll("terms", ""));
			}
			textPDF = new ArrayList<String>(textPDF.subList(start,
					textPDF.size() - 1));
			int end = KeywordUtil.findKeyWEnd(textPDF);
			textPDF = new ArrayList<String>(textPDF.subList(0, end));
			seperator = KeywordUtil.findSep(textPDF);
			String akronom = "";
			String currKey = "";
			for (int ii = 0; ii < textPDF.size(); ii++) {
				if (textPDF.get(ii).equals(seperator)) {
					if (!akronom.isEmpty()) {
						currKey = currKey.replaceAll("(" + akronom + ")", "");
						currKey = currKey.replace(")", "");
					}
					currKey = currKey.replaceFirst("[^\\p{L}]+", "");
					currKey = currKey.trim();
					String normKey = currKey.replaceAll("[^\\p{L}]+", "");
					if ((!currKey.isEmpty()) && (!normKey.isEmpty())) {
						keywords.add(new Category(currKey, normKey, akronom));
					}
					akronom = "";
					currKey = "";

				} else if (textPDF.get(ii).contains("(")) {
					akronom = KeywordUtil.getAkronom(new ArrayList<String>(textPDF.subList(
							ii, textPDF.size())));
				} else {

					currKey = currKey + " " + textPDF.get(ii);

				}
			}
			if ((currKey.length() < 100) && (currKey.length() > 2)) {
				if ((currKey.charAt(currKey.length() - 1) == '1')
						&& (!currKey.isEmpty())) {
					currKey = currKey.replace("1", "");
				}
				if (!akronom.isEmpty()) {
					currKey = currKey.replaceAll("(" + akronom + ")", "");
					currKey = currKey.replace(")", "");
				}
				currKey = currKey.replaceFirst("[^\\p{L}]+", "");
				if (currKey.endsWith(".")) {
					currKey = currKey.substring(0, currKey.length() - 1);
				}
				currKey = currKey.trim();
				String normKey = currKey.replaceAll("[^\\p{L}]+", "");
				if ((!currKey.isEmpty()) && (!normKey.isEmpty())) {
					keywords.add(new Category(currKey, normKey, akronom));
				}
			}
		}

		setCatnumb(keywords.size());
		try {
			LogUtil.writelog(keywords, name, seperator, textPDF.size(),this.language);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return keywords;
	}
}