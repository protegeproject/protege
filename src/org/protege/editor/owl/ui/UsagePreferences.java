package org.protege.editor.owl.ui;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;/*
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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: May 6, 2008<br><br>
 */
public class UsagePreferences {

    private String FILTER_SUBS = "filter.class.subs";
    private String FILTER_DISJOINTS = "filter.class.disjoints";

    private boolean filterSimpleSubclassAxioms;
    private boolean filterDisjointAxioms;

    private static UsagePreferences instance;

    public UsagePreferences() {
        load();
    }

    public static UsagePreferences getInstance(){
        if (instance == null){
            instance = new UsagePreferences();
        }
        return instance;
    }

    private Preferences getPreferences() {
        return PreferencesManager.getInstance().getApplicationPreferences(getClass());
    }


    private void load() {
        Preferences p = getPreferences();
        filterSimpleSubclassAxioms = p.getBoolean(FILTER_SUBS, false);
        filterDisjointAxioms = p.getBoolean(FILTER_DISJOINTS, false);
    }

    public boolean getFilterSimpleSubclassAxioms(){
        return filterSimpleSubclassAxioms;
    }

    public boolean getFilterDisjointAxioms(){
        return filterDisjointAxioms;
    }

    public void setFilterSimpleSubclassAxioms(boolean filter){
        filterSimpleSubclassAxioms = filter;
        getPreferences().putBoolean(FILTER_SUBS, filter);
    }

    public void setFilterDisjointAxioms(boolean filter){
        filterDisjointAxioms = filter;
        getPreferences().putBoolean(FILTER_DISJOINTS, filter);
    }
}
