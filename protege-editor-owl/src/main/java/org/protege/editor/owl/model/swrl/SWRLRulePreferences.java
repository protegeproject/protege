package org.protege.editor.owl.model.swrl;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;

public class SWRLRulePreferences {

	private static SWRLRulePreferences instance;

	private static final String SWRL_RULE_PREFS_KEY = "SWRL_RULE_PREFS";
	
	private static final String SORT_ON_ANN_PRP_KEY = "SORT_ON_ANN_PRP";
	private static final String SHOW_ANN_PRP_WITH_RULE_KEY = "SHOW_ANN_PRP_WITH_RULE";
	
	public static SWRLRulePreferences getInstance() {
		if (instance == null)
			instance = new SWRLRulePreferences();
		
		return instance;
	}
	
    private static Preferences getPreferences() {
        return PreferencesManager.getInstance().getApplicationPreferences(SWRL_RULE_PREFS_KEY);
    }

    public boolean getSortRulesOnAnnPrp() {
    	return getPreferences().getBoolean(SORT_ON_ANN_PRP_KEY, false);
    }
    
    public void setSortRulesOnAnnPrp(boolean sort) {
    	getPreferences().putBoolean(SORT_ON_ANN_PRP_KEY, sort);
    }
    
    public boolean getShowAnnPrpWithRule() {
    	return getPreferences().getBoolean(SHOW_ANN_PRP_WITH_RULE_KEY, false);
    }
    
    public void setShowAnnPrpWithRule(boolean show) {
    	getPreferences().putBoolean(SHOW_ANN_PRP_WITH_RULE_KEY, show);
    }
}
