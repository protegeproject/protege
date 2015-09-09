package org.protege.editor.owl.model.axiom;

import org.protege.editor.core.prefs.PreferencesManager;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 27/05/2014
 */
public class FreshAxiomLocationPreferences {

    private static final String PREFERENCES_KEY = "org.protege.editor.owl.axiom.newaxioms";

    private static final String FRESH_AXIOM_LOCATION_KEY = "fresh.axiom.location";

    private static FreshAxiomLocationPreferences instance;

    private FreshAxiomLocationPreferences() {
        load();
    }

    private void load() {
    }


    public void setFreshAxiomLocation(FreshAxiomLocation freshAxiomLocation) {
        PreferencesManager.getInstance().getApplicationPreferences(PREFERENCES_KEY).putString(
                FRESH_AXIOM_LOCATION_KEY,
                freshAxiomLocation.getLocationName());
    }

    public FreshAxiomLocation getFreshAxiomLocation() {
        String locationName = PreferencesManager.getInstance().getApplicationPreferences(PREFERENCES_KEY)
                .getString(FRESH_AXIOM_LOCATION_KEY, null);
        if(locationName == null) {
            return FreshAxiomLocation.getDefaultValue();
        }
        return FreshAxiomLocation.getLocationFromName(locationName).or(FreshAxiomLocation.getDefaultValue());
    }

    public static synchronized FreshAxiomLocationPreferences getPreferences() {
        if(instance == null) {
            instance = new FreshAxiomLocationPreferences();
        }
        return instance;
    }
}
