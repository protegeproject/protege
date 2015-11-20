package org.protege.editor.owl.ui.usage;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;

import java.util.HashSet;
import java.util.Set;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: May 6, 2008<br><br>
 */
public class UsagePreferences {

    private static UsagePreferences instance;

    public static UsagePreferences getInstance(){
        if (instance == null){
            instance = new UsagePreferences();
        }
        return instance;
    }

    private Preferences getPreferences() {
        return PreferencesManager.getInstance().getApplicationPreferences(getClass());
    }

    public boolean isFilterActive(UsageFilter filter){
        return getPreferences().getBoolean(filter.getKey(), false);
    }

    public void setFilterActive(UsageFilter filter, boolean active){
        getPreferences().putBoolean(filter.getKey(), active);
    }


    public Set<UsageFilter> getActiveFilters() {
        Set<UsageFilter> activeFilters = new HashSet<UsageFilter>();
        for (UsageFilter filter : UsageFilter.values()){
            if (isFilterActive(filter)){
                activeFilters.add(filter);
            }
        }
        return activeFilters;
    }
}
