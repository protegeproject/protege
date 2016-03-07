package org.protege.editor.owl.ui.tree;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 18-Oct-2007<br><br>
 */
public class OWLTreePreferences {

    private static OWLTreePreferences instance;

    private static final String TREE_PREFS_KEY = "TREE_PREFERENCES";

    private static final String AUTO_EXPAND_ENABLED_KEY = "AUTO_EXPAND_ENABLED_KEY";

    private static final String CHILD_COUNT_LIMIT_KEY = "AUTO_EXPANSION_CHILD_LIMIT";

    private static final String TREE_DEPTH_LIMIT_KEY = "TREE_DEPTH_LIMIT";

    private static final String TREE_DRAG_AND_DROP_ENABLED_KEY = "TREE_DRAG_AND_DROP_ENABLED";

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
        return getPreferences().getBoolean(AUTO_EXPAND_ENABLED_KEY, false);
    }

    public void setAutoExpansionEnabled(boolean enabled) {
        getPreferences().putBoolean(AUTO_EXPAND_ENABLED_KEY, enabled);
    }

    public boolean isTreeDragAndDropEnabled() {
        return getPreferences().getBoolean(TREE_DRAG_AND_DROP_ENABLED_KEY, true);
    }

    public void setTreeDragAndDropEnabled(boolean enabled) {
        getPreferences().putBoolean(TREE_DRAG_AND_DROP_ENABLED_KEY, enabled);
    }


    private static Preferences getPreferences() {
        return PreferencesManager.getInstance().getApplicationPreferences(TREE_PREFS_KEY);
    }




}
