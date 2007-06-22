package org.protege.editor.core;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 12-May-2007<br><br>
 */
public class BookMarkedURIManager {

    public static final String PREFERENCES_KEY = "BookMarkedURIs";

    private static BookMarkedURIManager instance;


    private BookMarkedURIManager() {
    }


    public static synchronized BookMarkedURIManager getInstance() {
        if (instance == null) {
            instance = new BookMarkedURIManager();
        }
        return instance;
    }


    public Set<URI> getBookMarkedURIs() {
        PreferencesManager man = PreferencesManager.getInstance();
        Preferences prefs = man.getApplicationPreferences(PREFERENCES_KEY);
        List<String> bookmarks = prefs.getStringList(PREFERENCES_KEY, getDefaults());
        Set<URI> uris = new HashSet<URI>();
        for (String s : bookmarks) {
            try {
                uris.add(new URI(s));
            }
            catch (URISyntaxException e) {
                ProtegeApplication.getErrorLog().handleError(Thread.currentThread(), e);
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
        List<String> defaults = new ArrayList<String>();
        defaults.add("http://owl.man.ac.uk/2006/07/sssw/people.owl");
        defaults.add("http://www.co-ode.org/ontologies/pizza/pizza.owl");
        defaults.add("http://protege.cim3.net/file/pub/ontologies/koala/koala.owl");
        defaults.add("http://protege.cim3.net/file/pub/ontologies/camera/camera.owl");
        defaults.add("http://protege.cim3.net/file/pub/ontologies/ka/ka.owl");
        defaults.add("http://protege.cim3.net/file/pub/ontologies/travel/travel.owl");
        defaults.add("http://www.w3.org/TR/owl-guide/wine.rdf");

        return defaults;
    }
}
