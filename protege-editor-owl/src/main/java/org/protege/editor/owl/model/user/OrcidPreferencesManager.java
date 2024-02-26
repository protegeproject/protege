package org.protege.editor.owl.model.user;


import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;

import org.protege.editor.core.prefs.Preferences;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 23/11/15
 */
public class OrcidPreferencesManager implements OrcidProvider {

    private static final String PREFERENCES_KEY = "user.orcid";

    private final Preferences preferences;

    /**
     * Create a manager to get and set the orcid preferences in the specified preferences object.
     * @param preferences The preferences object.  Not {@code null}.
     */
    public OrcidPreferencesManager(Preferences preferences) {
        this.preferences = checkNotNull(preferences);
    }

    /**
     * Gets the ORCID.
     * @return An optional ORCID.
     */
    public Optional<Orcid> getOrcid() {
        String orcid = preferences.getString(PREFERENCES_KEY, null);
        if(orcid == null) {
            return Optional.empty();
        }
        if("".equals(orcid)) {
            return Optional.empty();
        }
        return Optional.ofNullable(new Orcid(orcid));
    }

    /**
     * Sets the ORCID.
     * @param orcid The ORCID.  Not {@code null}.
     */
    public void setOrcid(Orcid orcid) {
        preferences.putString(PREFERENCES_KEY, orcid.getValue());
    }

    /**
     * Clears the ORCID.
     */
    public void clearOrcid() {
        preferences.putString(PREFERENCES_KEY, null);
    }
}
