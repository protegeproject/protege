package org.protege.editor.owl.ui.explanation;

import java.util.Collections;
import java.util.List;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;

public class ExplanationPreferences {

	private static final String PREFERENCES_SET_KEY_ = "EXPLANATION_PREFS_SET",
			DEFAULT_EXPLANATION_ID_ = "PREFERRED_PLUGIN_ID",
			USE_LAST_EXPLANATION_SERVICE_KEY_ = "USE_LAST_EXPLANATION_SERVICE",
			EXPLANATION_SERVICES_LIST_KEY_ = "EXPLANATION_SERVICES_LIST",
			DISABLED_EXPLANATION_SERVICES_KEY_ = "DISABLED_EXPLANATION_SERVICES";

	private final static String DEFAULT_DEFAULT_EXPLANATION_ID_ = null;
	private final static boolean DEFAULT_USE_LAST_EXPLANATION_SERVICE_ = true;
	private final static List<String> DEFAULT_EXPLANATION_SERVICES_LIST_ = Collections.emptyList();
	private final static List<String> DEFAULT_DISABLED_EXPLANATION_SERVICES_ = Collections.emptyList();

	public String defaultExplanationService;
	public boolean useLastExplanationService;
	public List<String> explanationServicesList;
	public List<String> disabledExplanationServices;

	private ExplanationPreferences() {
		// use create()
	}

	public static ExplanationPreferences create() {
		return new ExplanationPreferences().reset();
	}

	private static Preferences getPrefs() {
		PreferencesManager prefMan = PreferencesManager.getInstance();
		return prefMan.getPreferencesForSet(PREFERENCES_SET_KEY_, ExplanationPreferences.class);
	}

	public ExplanationPreferences load() {
		Preferences prefs = getPrefs();
		defaultExplanationService = prefs.getString(DEFAULT_EXPLANATION_ID_, DEFAULT_DEFAULT_EXPLANATION_ID_);
		useLastExplanationService = prefs.getBoolean(USE_LAST_EXPLANATION_SERVICE_KEY_,
				DEFAULT_USE_LAST_EXPLANATION_SERVICE_);
		explanationServicesList = prefs.getStringList(EXPLANATION_SERVICES_LIST_KEY_,
				DEFAULT_EXPLANATION_SERVICES_LIST_);
		disabledExplanationServices = prefs.getStringList(DISABLED_EXPLANATION_SERVICES_KEY_,
				DEFAULT_DISABLED_EXPLANATION_SERVICES_);
		return this;
	}

	public ExplanationPreferences save() {
		Preferences prefs = getPrefs();
		prefs.putString(DEFAULT_EXPLANATION_ID_, defaultExplanationService);
		prefs.putBoolean(USE_LAST_EXPLANATION_SERVICE_KEY_, useLastExplanationService);
		prefs.putStringList(EXPLANATION_SERVICES_LIST_KEY_, explanationServicesList);
		prefs.putStringList(DISABLED_EXPLANATION_SERVICES_KEY_, disabledExplanationServices);
		return this;
	}

	public ExplanationPreferences reset() {
		defaultExplanationService = DEFAULT_DEFAULT_EXPLANATION_ID_;
		useLastExplanationService = DEFAULT_USE_LAST_EXPLANATION_SERVICE_;
		explanationServicesList = DEFAULT_EXPLANATION_SERVICES_LIST_;
		disabledExplanationServices = DEFAULT_DISABLED_EXPLANATION_SERVICES_;
		return this;
	}

}
