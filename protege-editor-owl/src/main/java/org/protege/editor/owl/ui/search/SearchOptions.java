package org.protege.editor.owl.ui.search;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 29/10/2012
 */
public class SearchOptions {

    public static final boolean USE_REGEX_DEFAULT_SETTING = false;

    public static final boolean DEFAULT_CASE_SENSITIVE_SETTING = false;

    public static final boolean DEFAULT_IGNORE_WHITE_SPACE_SETTING = false;


    public static final boolean DEFAULT_WHOLE_WORDS_SETTING = false;

    public static final boolean DEFAULT_SHOW_ALL_RESULTS_SETTING = false;

    public static final boolean DEFAULT_SEARCH_IN_ANNOTATION_VALUES_SETTING = true;

    public static final boolean DEFAULT_SEARCH_IN_LOGICAL_AXIOM_SETTING = true;

    public static final boolean DEFAULT_SEARCH_IN_IRIS_SETTING = true;

    public static final boolean DEFAULT_SHOW_DEPRECATED_ENTITIES = true;


    private final boolean useRegex;

    private final boolean caseSensitive;

    private final boolean wholeWords;

    private final boolean ignoreWhiteSpace;


    private final boolean showAllResults;


    private final boolean searchInAnnotationValues;

    private final boolean searchInLogicalAxioms;

    private final boolean searchInIRIs;

    private final boolean showDeprecatedEntities;


    public SearchOptions() {
        useRegex = USE_REGEX_DEFAULT_SETTING;
        caseSensitive = DEFAULT_CASE_SENSITIVE_SETTING;
        wholeWords = DEFAULT_WHOLE_WORDS_SETTING;
        ignoreWhiteSpace = DEFAULT_IGNORE_WHITE_SPACE_SETTING;
        showAllResults = DEFAULT_SHOW_ALL_RESULTS_SETTING;
        searchInAnnotationValues = DEFAULT_SEARCH_IN_ANNOTATION_VALUES_SETTING;
        searchInLogicalAxioms = DEFAULT_SEARCH_IN_LOGICAL_AXIOM_SETTING;
        searchInIRIs = DEFAULT_SEARCH_IN_IRIS_SETTING;
        showDeprecatedEntities = DEFAULT_SHOW_DEPRECATED_ENTITIES;
    }

    private SearchOptions(Builder builder) {
        useRegex = builder.isUseRegex();
        caseSensitive = builder.isCaseSensitive();
        wholeWords = builder.isWholeWords();
        ignoreWhiteSpace = builder.isIgnoreWhiteSpace();
        showAllResults = builder.isShowAllResults();
        searchInAnnotationValues = builder.isSearchInAnnotationValues();
        searchInLogicalAxioms = builder.isSearchInLogicalAxioms();
        searchInIRIs = builder.isSearchInIRIs();
        showDeprecatedEntities = builder.isShowDeprecatedEntities();
    }

    public boolean isUseRegex() {
        return useRegex;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public boolean isWholeWords() {
        return wholeWords;
    }

    public boolean isIgnoreWhiteSpace() {
        return ignoreWhiteSpace;
    }

    public boolean isShowAllResults() {
        return showAllResults;
    }

    public boolean isSearchInAnnotationValues() {
        return searchInAnnotationValues;
    }

    public boolean isSearchInLogicalAxioms() {
        return searchInLogicalAxioms;
    }

    public boolean isSearchInIRIs() {
        return searchInIRIs;
    }

    public boolean isShowDeprecatedEntities() {
        return showDeprecatedEntities;
    }

    public static class Builder {


        private boolean useRegex = USE_REGEX_DEFAULT_SETTING;

        private boolean caseSensitive = DEFAULT_CASE_SENSITIVE_SETTING;

        private boolean wholeWords = DEFAULT_WHOLE_WORDS_SETTING;

        private boolean ignoreWhiteSpace = DEFAULT_IGNORE_WHITE_SPACE_SETTING;


        private boolean showAllResults;


        private boolean searchInAnnotationValues;

        private boolean searchInLogicalAxioms;

        private boolean searchInIRIs;

        private boolean showDeprecatedEntities;

        public SearchOptions build() {
            return new SearchOptions(this);
        }

        public void setUseRegex(boolean useRegex) {
            this.useRegex = useRegex;
        }

        public void setCaseSensitive(boolean caseSensitive) {
            this.caseSensitive = caseSensitive;
        }

        public void setWholeWords(boolean wholeWords) {
            this.wholeWords = wholeWords;
        }

        public void setIgnoreWhiteSpace(boolean ignoreWhiteSpace) {
            this.ignoreWhiteSpace = ignoreWhiteSpace;
        }

        public void setShowAllResults(boolean showAllResults) {
            this.showAllResults = showAllResults;
        }

        public void setSearchInAnnotationValues(boolean searchInAnnotationValues) {
            this.searchInAnnotationValues = searchInAnnotationValues;
        }

        public void setSearchInLogicalAxioms(boolean searchInLogicalAxioms) {
            this.searchInLogicalAxioms = searchInLogicalAxioms;
        }

        public void setSearchInIRIs(boolean searchInIRIs) {
            this.searchInIRIs = searchInIRIs;
        }

        public void setShowDeprecatedEntities(boolean showDeprecatedEntities) {
            this.showDeprecatedEntities = showDeprecatedEntities;
        }

        public boolean isUseRegex() {
            return useRegex;
        }

        public boolean isCaseSensitive() {
            return caseSensitive;
        }

        public boolean isWholeWords() {
            return wholeWords;
        }

        public boolean isIgnoreWhiteSpace() {
            return ignoreWhiteSpace;
        }

        public boolean isShowAllResults() {
            return showAllResults;
        }

        public boolean isSearchInAnnotationValues() {
            return searchInAnnotationValues;
        }

        public boolean isSearchInLogicalAxioms() {
            return searchInLogicalAxioms;
        }

        public boolean isSearchInIRIs() {
            return searchInIRIs;
        }

        public boolean isShowDeprecatedEntities() {
            return showDeprecatedEntities;
        }
    }
}
