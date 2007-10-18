package org.protege.editor.owl.ui.tree;

import org.protege.editor.core.prefs.PreferencesManager;
import org.protege.editor.core.prefs.Preferences;
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
 * Date: 18-Oct-2007<br><br>
 */
public class OWLTreePreferences {

    private static OWLTreePreferences instance;

    private static final String TREE_PREFS_KEY = "TREE_PREFS";

    private static final String AUTO_EXPAND_ENABLED_KEY = "AUTO_EXPAND_ENABLED_KEY";

    private static final String CHILD_COUNT_LIMIT_KEY = "AUTO_EXPANSION_CHILD_LIMIT";

    private static final String TREE_DEPTH_LIMIT_KEY = "TREE_DEPTH_LIMIT";

    public static synchronized OWLTreePreferences getInstance() {
        if(instance == null) {
            instance = new OWLTreePreferences();
        }
        return instance;
    }


    /**
     * Gets the maximum child count that would allow a branch to be automatically
     * expanded.
     * @return The limit.
     */
    public int getAutoExpansionChildLimit() {
        return getPreferences().getInt(
                CHILD_COUNT_LIMIT_KEY, 50);
    }

    public void setAutoExpansionChildLimit(int limit) {
        getPreferences().putInt(CHILD_COUNT_LIMIT_KEY, limit);
    }

    public int getAutoExpansionDepthLimit() {
        return getPreferences().getInt(TREE_DEPTH_LIMIT_KEY, 3);
    }

    public void setAutoExpansionDepthLimit(int limit) {
        getPreferences().putInt(TREE_DEPTH_LIMIT_KEY, limit);
    }

    public boolean isAutoExpandEnabled() {
        return getPreferences().getBoolean(AUTO_EXPAND_ENABLED_KEY, true);
    }

    public void setAutoExpansionEnabled(boolean enabled) {
        getPreferences().putBoolean(AUTO_EXPAND_ENABLED_KEY, enabled);
    }


    private static Preferences getPreferences() {
        return PreferencesManager.getInstance().getApplicationPreferences(TREE_PREFS_KEY);
    }




}
