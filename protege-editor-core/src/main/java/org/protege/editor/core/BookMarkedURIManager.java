package org.protege.editor.core;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 12-May-2007<br><br>
 */
public class BookMarkedURIManager {

    public static final String PREFERENCES_KEY = "BookMarkedURIs";

    private static BookMarkedURIManager instance;

    private final Logger logger = LoggerFactory.getLogger(BookMarkedURIManager.class);


    private BookMarkedURIManager() {
    }


    public static synchronized BookMarkedURIManager getInstance() {
        if (instance == null) {
            instance = new BookMarkedURIManager();
        }
        return instance;
    }
    
    protected void dispose() {
        instance = null;
    }


    public Set<URI> getBookMarkedURIs() {
        PreferencesManager man = PreferencesManager.getInstance();
        Preferences prefs = man.getApplicationPreferences(PREFERENCES_KEY);
        List<String> bookmarks = prefs.getStringList(PREFERENCES_KEY, getDefaults());
        Set<URI> uris = new HashSet<>();
        for (String s : bookmarks) {
            try {
                uris.add(new URI(s));
            }
            catch (URISyntaxException e) {
                logger.warn("Bookmarked URI syntax error: {}", e.getMessage());
            }
        }
        return uris;
    }


    public void remove(URI bookMarkedURI) {
        PreferencesManager man = PreferencesManager.getInstance();
        Preferences prefs = man.getApplicationPreferences(PREFERENCES_KEY);
        List<String> bookmarks = prefs.getStringList(PREFERENCES_KEY, getDefaults());
        bookmarks.remove(bookMarkedURI.toString());
        prefs.putStringList(PREFERENCES_KEY, bookmarks);
    }


    public void add(URI bookMarkedURI) {
        PreferencesManager man = PreferencesManager.getInstance();
        Preferences prefs = man.getApplicationPreferences(PREFERENCES_KEY);
        List<String> bookmarks = prefs.getStringList(PREFERENCES_KEY, getDefaults());
        bookmarks.add(bookMarkedURI.toString());
        prefs.putStringList(PREFERENCES_KEY, bookmarks);
    }


    private static List<String> getDefaults() {
        List<String> defaults = new ArrayList<>();
        defaults.add("http://protege.stanford.edu/ontologies/pizza/pizza.owl");
        defaults.add("http://owl.man.ac.uk/2006/07/sssw/people.owl");
        defaults.add("http://protege.stanford.edu/ontologies/koala.owl");
        defaults.add("http://protege.stanford.edu/ontologies/camera.owl");
        defaults.add("http://protege.stanford.edu/ontologies/travel.owl");
        defaults.add("http://www.w3.org/TR/owl-guide/wine.rdf");

        return defaults;
    }
}
