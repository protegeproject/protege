package org.protege.editor.owl.model.search;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 04/11/2015
 */
public class SearchKeyword implements SearchInput {

    public enum Occurance { INCLUDE, EXCLUDE, OPTIONAL }

    private String field;
    private String keyword;

    private Occurance occurance = Occurance.OPTIONAL;
    private boolean isCaseSensitive = false;
    private boolean ignoreWhitespace = true;
    private boolean searchWholeWords = false;
    private boolean searchByRegex = false;
    private boolean searchByPhonetic = false;

    public SearchKeyword(String field, String keyword, Occurance occurance, boolean isCaseSensitive, boolean ignoreWhitespace,
            boolean searchWholeWords, boolean searchByRegex, boolean searchByPhonetic) {
        this.field = field;
        this.keyword = keyword;
        this.occurance = occurance;
        this.isCaseSensitive = isCaseSensitive;
        this.ignoreWhitespace = ignoreWhitespace;
        this.searchWholeWords = searchWholeWords;
        this.searchByRegex = searchByRegex;
        this.searchByPhonetic = searchByPhonetic;
    }

    public SearchKeyword(String field, String keyword) {
        this.field = field;
        this.keyword = keyword;
    }

    public SearchKeyword(String keyword) {
        this("", keyword);
    }

    public boolean isBlank() {
        return keyword == null || keyword.trim().isEmpty();
    }

    public boolean hasField() {
        return field != null && !field.trim().isEmpty();
    }

    public String getField() {
        return field;
    }

    public String getString() {
        return keyword;
    }

    public Occurance occurance() {
        return occurance;
    }

    public boolean isCaseSensitive() {
        return isCaseSensitive;
    }

    public boolean isIgnoreWhitespace() {
        return ignoreWhitespace;
    }

    public boolean searchWholeWords() {
        return searchWholeWords;
    }

    public boolean searchByRegex() {
        return searchByRegex;
    }

    public boolean searchByPhonetic() {
        return searchByPhonetic;
    }

    @Override
    public String asSearchString() {
        String buffer = keyword;
        if (searchWholeWords || searchByRegex) {
            buffer = "\"" + buffer + "\"";
        }
        if (searchByPhonetic) {
            buffer = "~" + buffer;
        }
        if (occurance == Occurance.INCLUDE) {
            buffer = "+" + buffer;
        } else if (occurance == Occurance.EXCLUDE) {
            buffer = "-" + buffer;
        }
        if (hasField()) {
            buffer = field + ":" + buffer;
        }
        return buffer;
    }

    @Override
    public String toString() {
        return asSearchString();
    }
}
