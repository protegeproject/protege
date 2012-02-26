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


    public static final String DEFAULT_BASE = "http://www.semanticweb.org";

    public static final String PREFERENCES_KEY = "OntologyPreferences";

    public static final String BASE_URI_KEY = "BASE_URI";

    public static final String INC_YEAR_KEY = "INC_YEAR";

    public static final String INC_MONTH_KEY = "INC_MONTH";

    public static final String INC_DAY_KEY = "INC_DAY";
    
    public static final String INC_DOCUMENT_COUNTER = "INC_DOCUMENT_COUNTER";
    
    public static final String DOCUMENT_COUNTER = "DOCUMENT_COUNTER";

    private static OntologyPreferences instance;

    public static final String SYSTEM_USER_NAME_PROPERTY = "user.name";

    public static final int DOCUMENT_COUNTER_START = 1;


    private URI baseURI;

    private boolean includeYear = false;

    private boolean includeMonth = false;

    private boolean includeDay = false;
    
    private boolean useCounter = true;


    private OntologyPreferences() {
        StringBuilder sb = new StringBuilder();
        sb.append(DEFAULT_BASE);
        String userName = System.getProperty(SYSTEM_USER_NAME_PROPERTY);
        if(userName != null) {
            sb.append("/");
            sb.append(userName);
        }
        sb.append("/ontologies");
        baseURI = URI.create(sb.toString());
    }


    public static synchronized OntologyPreferences getInstance() {
        if (instance == null) {
            instance = new OntologyPreferences();
            instance.restore();
        }
        return instance;
    }


    private void restore() {
        Preferences p = getPreferences();
        baseURI = URI.create(p.getString(BASE_URI_KEY, baseURI.toString()));
        includeYear = p.getBoolean(INC_YEAR_KEY, true);
        includeMonth = p.getBoolean(INC_MONTH_KEY, true);
        includeDay = p.getBoolean(INC_DAY_KEY, false);
        useCounter = p.getBoolean(INC_DOCUMENT_COUNTER, true);
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
        Preferences p = getPreferences();
        p.putString(BASE_URI_KEY, baseURI.toString());
        p.putBoolean(INC_YEAR_KEY, includeYear);
        p.putBoolean(INC_MONTH_KEY, includeMonth);
        p.putBoolean(INC_DAY_KEY, includeDay);
    }

    private Preferences getPreferences() {
        return PreferencesManager.getInstance().getApplicationPreferences(PREFERENCES_KEY);
    }

    public int getCounter() {
        return getPreferences().getInt(DOCUMENT_COUNTER, DOCUMENT_COUNTER_START);
    }
    
    public void incrementCounter() {
        Preferences preferences = getPreferences();
        int counter = preferences.getInt(DOCUMENT_COUNTER, DOCUMENT_COUNTER_START);
        counter++;
        preferences.putInt(DOCUMENT_COUNTER, counter);
        save();
    }

    public void resetDocumentCounter() {
        getPreferences().putInt(DOCUMENT_COUNTER, DOCUMENT_COUNTER_START);
        save();
    }

    public URI generateNextURI() {
        incrementCounter();
        return generateURI();
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
        String documentName = "untitled-ontology-" + getCounter();
        uriString += documentName;
        return URI.create(uriString);
    }
}
