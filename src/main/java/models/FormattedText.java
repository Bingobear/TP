package models;

import Util.WordTypeFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by simonbruns on 13/05/16.
 */
public class FormattedText {
    private ArrayList<Word> words = new ArrayList<Word>();
    private List<WordTypeFilter> filteredWordTypes;

    public FormattedText(List<WordTypeFilter> wordTypes, ArrayList<Word> words) {
        this.filteredWordTypes = wordTypes;
        this.words = words;
    }

}
