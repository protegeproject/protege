package org.protege.editor.core.ui.workspace;

import java.util.ArrayList;
import java.util.List;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Medical Informatics Group<br> Date:
 * 06-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br> www.cs.man.ac.uk/~horridgm<br><br>
 */
public class TabbedWorkspaceStateManager extends DefaultHandler {

    public static final String TABS_PREFERENCES_SET = "tabs";

    public static final String VISIBLE_TABS_PREFERENCE_KEY = "visible_tabs";

    private List<String> tabs;


    public TabbedWorkspaceStateManager() {
        tabs = new ArrayList<>();
        load();
    }


    public TabbedWorkspaceStateManager(TabbedWorkspace workspace) {
        tabs = new ArrayList<>();
        for (WorkspaceTab tab : workspace.getWorkspaceTabs()) {
            tabs.add(tab.getId());
        }
    }


    public List<String> getTabs() {
        return new ArrayList<>(tabs);
    }


    public void load() {
        tabs.clear();
        tabs.addAll(getPreferences().getStringList(VISIBLE_TABS_PREFERENCE_KEY, new ArrayList<>()));
    }


    public void save() {
        getPreferences().putStringList(VISIBLE_TABS_PREFERENCE_KEY, tabs);
    }


    protected static Preferences getPreferences() {
        return PreferencesManager.getInstance().getApplicationPreferences(TABS_PREFERENCES_SET);
    }
}
