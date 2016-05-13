package models;

import java.util.ArrayList;

/**
 * Created by simonbruns on 13/05/16.
 */
public class FormattedText {
    private ArrayList<Word> words = new ArrayList<Word>();
    private boolean isNoun=false;
    private boolean isNoun_Verb=false;
    private boolean isNoun_Verb_Adjective=false;

    public FormattedText(int filterWordtypeMode, ArrayList<Word> words) {
        this.words = words;
        if(filterWordtypeMode==0){
            setNoun(true);
        }else if(filterWordtypeMode==1){
            setNoun_Verb(true);
        }else if (filterWordtypeMode==2){
            setNoun_Verb_Adjective(true);
        }
    }

    public boolean isNoun() {
        return isNoun;
    }

    public void setNoun(boolean noun) {
        isNoun = noun;
    }

    public boolean isNoun_Verb() {
        return isNoun_Verb;
    }

    public void setNoun_Verb(boolean noun_Verb) {
        isNoun_Verb = noun_Verb;
    }

    public boolean isNoun_Verb_Adjective() {
        return isNoun_Verb_Adjective;
    }

    public void setNoun_Verb_Adjective(boolean noun_Verb_Adjective) {
        isNoun_Verb_Adjective = noun_Verb_Adjective;
    }
}
