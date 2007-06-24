package org.protege.editor.owl.ui.ontology;

import java.net.URI;
import java.util.Calendar;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 28-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OntologyPreferences {


    public static final String DEFAULT_BASE = "http://www.semanticweb.org/ontologies";

    public static final String PREFERENCES_KEY = "OntologyPreferences";

    public static final String BASE_URI_KEY = "BASE_URI";

    public static final String INC_YEAR_KEY = "INC_YEAR";

    public static final String INC_MONTH_KEY = "INC_MONTH";

    public static final String INC_DAY_KEY = "INC_DAY";

    private static OntologyPreferences instance;


    private URI baseURI;

    private boolean includeYear;

    private boolean includeMonth;

    private boolean includeDay;


    private OntologyPreferences() {
        baseURI = URI.create(DEFAULT_BASE);
        includeYear = true;
        includeMonth = true;
        includeDay = false;
    }


    public static synchronized OntologyPreferences getInstance() {
        if (instance == null) {
            instance = new OntologyPreferences();
            instance.restore();
        }
        return instance;
    }


    private void restore() {
        Preferences p = PreferencesManager.getInstance().getApplicationPreferences(PREFERENCES_KEY);
        baseURI = URI.create(p.getString(BASE_URI_KEY, DEFAULT_BASE));
        includeYear = p.getBoolean(INC_YEAR_KEY, true);
        includeMonth = p.getBoolean(INC_MONTH_KEY, true);
        includeDay = p.getBoolean(INC_DAY_KEY, false);
    }


    public URI getBaseURI() {
        return baseURI;
    }


    public void setBaseURI(URI baseURI) {
        this.baseURI = baseURI;
        save();
    }


    public boolean isIncludeYear() {
        return includeYear;
    }


    public void setIncludeYear(boolean includeYear) {
        this.includeYear = includeYear;
        save();
    }


    public boolean isIncludeMonth() {
        return includeMonth;
    }


    public void setIncludeMonth(boolean includeMonth) {
        this.includeMonth = includeMonth;
        save();
    }


    public boolean isIncludeDay() {
        return includeDay;
    }


    public void setIncludeDay(boolean includeDay) {
        this.includeDay = includeDay;
        save();
    }


    private void save() {
        Preferences p = PreferencesManager.getInstance().getApplicationPreferences(PREFERENCES_KEY);
        p.putString(BASE_URI_KEY, baseURI.toString());
        p.putBoolean(INC_YEAR_KEY, includeYear);
        p.putBoolean(INC_MONTH_KEY, includeMonth);
        p.putBoolean(INC_DAY_KEY, includeDay);
    }


    public URI generateURI() {
        String uriString = baseURI.toString();
        if (!uriString.endsWith("/")) {
            uriString = uriString + "/";
        }
        if (includeYear) {
            uriString += Calendar.getInstance().get(Calendar.YEAR) + "/";
            if (includeMonth) {
                uriString += Calendar.getInstance().get(Calendar.MONTH) + "/";
                if (includeDay) {
                    uriString += Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "/";
                }
            }
        }
        String name = "Ontology" + System.currentTimeMillis() + ".owl";
        uriString += name;
        return URI.create(uriString);
    }
}
