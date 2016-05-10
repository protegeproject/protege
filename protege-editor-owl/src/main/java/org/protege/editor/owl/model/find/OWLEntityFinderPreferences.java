package org.protege.editor.owl.model.find;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 03-Sep-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLEntityFinderPreferences {

    public static final String PREFERENCES_KEY = "org.protege.editor.owl.finder";

    public static final String USE_REGULAR_EXPRESSIONS_KEY = "USE_REGULAR_EXPRESSIONS";

    public static final String SEARCH_DELAY_KEY = "SEARCH_DELAY_KEY";

    public static final String CASE_SENSITIVE_KEY = "CASE_SENSITIVE_KEY";

    public static final String WHOLE_WORDS_KEY = "WHOLE_WORDS_KEY";

    public static final String IGNORE_WHITE_SPACE_KEY = "IGNORE_WHITE_SPACE_KEY";


    private static final boolean DEFAULT_CASE_SENSITIVE_VALUE = false;


    private static OWLEntityFinderPreferences instance;

    private boolean useRegularExpressions;

    private long searchDelay;

    private boolean caseSensitive;

    private boolean wholeWords;

    private boolean ignoreWhiteSpace;


    private OWLEntityFinderPreferences() {
        searchDelay = 500;
        useRegularExpressions = false;
        caseSensitive = DEFAULT_CASE_SENSITIVE_VALUE;
        load();
    }


    private static Preferences getPreferences() {
        return PreferencesManager.getInstance().getApplicationPreferences(PREFERENCES_KEY);
    }


    private void load() {
        Preferences prefs = getPreferences();
        useRegularExpressions = prefs.getBoolean(USE_REGULAR_EXPRESSIONS_KEY, false);
        searchDelay = prefs.getLong(SEARCH_DELAY_KEY, 500);
        caseSensitive = prefs.getBoolean(CASE_SENSITIVE_KEY, DEFAULT_CASE_SENSITIVE_VALUE);
        wholeWords = prefs.getBoolean(WHOLE_WORDS_KEY, false);
        ignoreWhiteSpace = prefs.getBoolean(IGNORE_WHITE_SPACE_KEY, true);
    }


    public boolean isUseRegularExpressions() {
        return useRegularExpressions;
    }


    public void setUseRegularExpressions(boolean useRegularExpressions) {
        this.useRegularExpressions = useRegularExpressions;
        getPreferences().putBoolean(USE_REGULAR_EXPRESSIONS_KEY, useRegularExpressions);
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean ignoreCase) {
        this.caseSensitive = ignoreCase;
        getPreferences().putBoolean(CASE_SENSITIVE_KEY, ignoreCase);
    }

    public boolean isWholeWords() {
        return wholeWords;
    }

    public void setWholeWords(boolean wholeWords) {
        this.wholeWords = wholeWords;
        getPreferences().putBoolean(WHOLE_WORDS_KEY, wholeWords);
    }

    public boolean isIgnoreWhiteSpace() {
        return ignoreWhiteSpace;
    }

    public void setIgnoreWhiteSpace(boolean ignoreWhiteSpace) {
        this.ignoreWhiteSpace = ignoreWhiteSpace;
        getPreferences().putBoolean(IGNORE_WHITE_SPACE_KEY, ignoreWhiteSpace);
    }

    public long getSearchDelay() {
        return searchDelay;
    }


    public void setSearchDelay(long searchDelay) {
        this.searchDelay = searchDelay;
        getPreferences().putLong(SEARCH_DELAY_KEY, searchDelay);
    }


    public static synchronized OWLEntityFinderPreferences getInstance() {
        if (instance == null) {
            instance = new OWLEntityFinderPreferences();
        }
        return instance;
    }
}
