package org.protege.editor.owl.model.inference;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;

public class ReasonerPreferences {
    public static final String PREFERENCES_SET_KEY = "INFERENCE_PREFS_SET";

    public static final String SHOW_INFERENCES_KEY = "SHOW_INFERENCES";
    
	private boolean showInferences;
	
	public void load() {
        PreferencesManager prefMan = PreferencesManager.getInstance();
        Preferences prefs = prefMan.getPreferencesForSet(PREFERENCES_SET_KEY, ReasonerPreferences.class);
        showInferences = prefs.getBoolean(SHOW_INFERENCES_KEY, true);
	}
	
	public void save() {
        PreferencesManager prefMan = PreferencesManager.getInstance();
        Preferences prefs = prefMan.getPreferencesForSet(PREFERENCES_SET_KEY, ReasonerPreferences.class);
        prefs.putBoolean(SHOW_INFERENCES_KEY, showInferences);
	}

	public boolean isShowInferences() {
		return showInferences;
	}

	public void setShowInferences(boolean showInferences) {
		this.showInferences = showInferences;
	}

}
