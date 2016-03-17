package Util;

import java.util.ArrayList;

public class KeywordUtil {

    /**
     * extracts the separator (e.g. ",") from a given text (list)
     *
     * @param textPDF
     * @return
     */
    public static String findSep(ArrayList<String> textPDF) {
        String[] seperatorC = { ",", ";", ".", "-" };
        int[] occ = new int[seperatorC.length];
        for (int ii = 0; ii < occ.length; ii++) {
            occ[ii] = 0;
        }
        int sep = 0;
        for (int ii = 0; ii < textPDF.size(); ii++) {
            for (int counter = 0; counter < occ.length; counter++) {
                if (textPDF.get(ii).equals(seperatorC[counter])) {
                    occ[counter] = occ[counter] + 1;
                    if (occ[sep] < occ[counter]) {
                        sep = counter;
                    }
                }
            }
        }
        if (occ[sep] < 1) {
            return null;
        } else {
            return seperatorC[sep];
        }
    }

    /**
     * Identifies the probable start of the keyword enumeration
     *
     * @param textPDF
     * @return
     */
    public static int findKeyWStart(ArrayList<String> textPDF) {
        int start = -1;
        if (textPDF.contains("keywords")) {
            start = textPDF.indexOf("keywords") + 1;

//			System.out.println("Keyword found " + start);

        } else if (textPDF.contains("keyword")) {
            start = textPDF.indexOf("keyword") + 1;
        } else {
            start = getKeywPosition(textPDF);
//			System.out.println("Keyword found within word" + start);
        }
        if (textPDF.contains("index")) {
            // does not work i think
            int Istart = textPDF.indexOf("index");
            if (textPDF.get(start + 1).equals("terms")) {
                Istart = Istart + 2;
                if ((Istart < start) || (start == 0)) {
                    start = Istart;
                }
//				System.out.println("Index found " + start);
            } else if ((Istart < start) || (start == 0)) {
                start = findTermsposition(Istart + 1, new ArrayList<String>(
                        textPDF.subList(Istart + 1, textPDF.size())));
            }
        }
        return start;
    }

    /**
     * Identifies start of keywordenumeration (pdf uses as a term synonym)
     *
     * @param ostart
     * @param arrayList
     * @return
     */
    private static int findTermsposition(int ostart, ArrayList<String> arrayList) {
        for (int ii = 0; ii < arrayList.size(); ii++) {
            if ((arrayList.get(ii).contains("terms"))) {
                String word = arrayList.get(ii).replace("terms", "");
                if (word.length() > 1) {
                    // word that contains dot -> is itself a keyword
                    return ii + ostart;
                } else {
                    // word is a fragment with no meaning
                    return ii + 1 + ostart;
                }
            }
        }
        return 0;

    }

    /**
     * identifies keyword start via "keyword" position
     *
     * @param textPDF
     * @return
     * @return
     */
    private static int getKeywPosition(ArrayList<String> textPDF) {
        for (int ii = 0; ii < textPDF.size(); ii++) {
            if ((textPDF.get(ii).contains("keywords"))) {
                String word = textPDF.get(ii).replace("keywords", "");
                if (word.length() > 1) {
                    // word that contains dot -> is itself a keyword
                    return ii;
                } else {
                    // word is a fragment with no meaning
                    return ii + 1;
                }
            }
        }
        return -1;
    }

    /**
     * Identifies keyword end position
     *
     * @param textPDF
     * @return
     * @return
     */
    public static int findKeyWEnd(ArrayList<String> textPDF) {
        int end = textPDF.size() - 1;
        int endCandidate = 0;
        // ami2011 - towards 1s20 - ACM, WHEN- beul_et_al._ahfe.pdf, editorial -
        // iwc.iwt053.full.pdf
        String[] stops = { "introduction", "motivation", "abstract", ".",
                "acm", "towards", "when", "editorial", "*" };
        for (int ii = 0; ii < stops.length; ii++) {
            endCandidate = textPDF.indexOf(stops[ii]);
            if (textPDF.contains(stops[ii])) {
                if (stops[ii].equals(".")) {
                    int dotcandidate = findDotPosition(textPDF);
                    if (dotcandidate < endCandidate) {
                        endCandidate = dotcandidate;
                    }
                }
                if ((end > endCandidate) && (endCandidate > 4)) {
                    end = endCandidate;
//					System.out.println(stops[ii] + ": - " + end);
                }
            }
        }

        return end;

    }

    /**
     * Extracts the akronom from a given string e.g. technology acceptance (ta)
     * -> ta
     *
     * @param arrayList
     * @return
     */
    public static String getAkronom(ArrayList<String> arrayList) {
        int end = getEndBracketPos(arrayList);
        String akro = "";
        int start = 0;
        if (arrayList.get(0).equals("(")) {
            start++;
        }
        for (int ii = start; ii < end; ii++) {
            akro = akro + arrayList.get(ii) + " ";
        }
        if (!(arrayList.get(end).equals(")"))) {
            akro = akro + arrayList.get(end);
        }

        akro = akro.replace("(", "");
        akro = akro.replace(")", "");
        akro.trim();
        return akro;
    }

    private static int getEndBracketPos(ArrayList<String> arrayList) {
        for (int ii = 0; ii < arrayList.size(); ii++) {
            if (arrayList.get(ii).contains(")")) {
                return ii;
            }
        }
        return 0;
    }

    /**
     * Identifies dot position to resolve fragments or end
     *
     * @param textPDF
     * @return
     */
    public static int findDotPosition(ArrayList<String> textPDF) {
        for (int ii = 0; ii < textPDF.size(); ii++) {
            if ((textPDF.get(ii).contains("."))) {
                if (ii > 4) {
                    String word = textPDF.get(ii).replace(".", "");
                    if (word.length() > 1) {
                        // word that contains dot -> is itself a keyword
                        return ii + 1;
                    } else {
                        // word is a fragment with no meaning
                        return ii;
                    }
                } else
                    break;
            }
        }
        return 50;
    }

}
