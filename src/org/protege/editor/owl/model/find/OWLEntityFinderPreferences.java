package org.protege.editor.owl.model.find;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 03-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLEntityFinderPreferences {

    public static final String PREFERENCES_KEY = "org.protege.editor.owl.finder";

    public static final String USE_REGULAR_EXPRESSIONS_KEY = "USE_REGULAR_EXPRESSIONS";

    public static final String SEARCH_DELAY_KEY = "SEARCH_DELAY_KEY";

    private static OWLEntityFinderPreferences instance;

    private boolean useRegularExpressions;

    private long searchDelay;


    private OWLEntityFinderPreferences() {
        searchDelay = 500;
        useRegularExpressions = false;
        load();
    }


    private static Preferences getPreferences() {
        return PreferencesManager.getInstance().getApplicationPreferences(PREFERENCES_KEY);
    }


    private void load() {
        Preferences prefs = getPreferences();
        useRegularExpressions = prefs.getBoolean(USE_REGULAR_EXPRESSIONS_KEY, false);
        searchDelay = prefs.getLong(SEARCH_DELAY_KEY, 500);
    }


    public boolean isUseRegularExpressions() {
        return useRegularExpressions;
    }


    public void setUseRegularExpressions(boolean useRegularExpressions) {
        this.useRegularExpressions = useRegularExpressions;
        getPreferences().putBoolean(USE_REGULAR_EXPRESSIONS_KEY, useRegularExpressions);
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
