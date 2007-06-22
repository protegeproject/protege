package org.protege.editor.owl.model.find;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
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
 * Medical Informatics Group<br>
 * Date: 03-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class EntityFinderPreferences {

    public static final String PREFERENCES_KEY = "org.protege.editor.owl.finder";

    public static final String USE_REGULAR_EXPRESSIONS_KEY = "USE_REGULAR_EXPRESSIONS";

    public static final String SEARCH_DELAY_KEY = "SEARCH_DELAY_KEY";

    private static EntityFinderPreferences instance;

    private boolean useRegularExpressions;

    private long searchDelay;


    private EntityFinderPreferences() {
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


    public static synchronized EntityFinderPreferences getInstance() {
        if (instance == null) {
            instance = new EntityFinderPreferences();
        }
        return instance;
    }
}
