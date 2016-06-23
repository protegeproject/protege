package org.protege.editor.owl.model.search;

/**
 * @author Josef Hardi <johardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 22/06/2016
 */
public class SearchKeyword {

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
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SearchKeyword)) {
            return false;
        }
        SearchKeyword other = (SearchKeyword) obj;
        return field.equals(other.field) && keyword.equals(other.keyword) && occurance.equals(other.occurance)
                && isCaseSensitive == other.isCaseSensitive && ignoreWhitespace == other.ignoreWhitespace
                && searchWholeWords == other.searchWholeWords && searchByRegex == other.searchByRegex
                && searchByPhonetic == other.searchByPhonetic;
    }

    @Override
    public int hashCode() {
        int hashCode = 31 * field.hashCode() + keyword.hashCode() + occurance.hashCode();
        hashCode += (isCaseSensitive ? 1 : 0);
        hashCode += (ignoreWhitespace ? 1 : 0);
        hashCode += (searchWholeWords ? 1 : 0);
        hashCode += (searchByRegex ? 1 : 0);
        hashCode += (searchByPhonetic ? 1 : 0);
        return hashCode;
    }

    @Override
    public String toString() {
        String buffer = keyword;
        if (searchWholeWords || searchByRegex) {
            buffer = "\"" + buffer + "\"";
        }
        if (hasField()) {
            buffer = field + ":" + buffer;
        }
        return buffer;
    }
}
