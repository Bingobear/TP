package Util;

/**
 * Created by simonbruns on 03/03/2017.
 */
public enum WordTypeFilter {
    NOUN("NN"), VERB("VB"), ADJECTIVE("JJ");

    private String types;

    WordTypeFilter(String types) {
        this.types = types;
    }

    public String getTypes() {
        return types;
    }
}
