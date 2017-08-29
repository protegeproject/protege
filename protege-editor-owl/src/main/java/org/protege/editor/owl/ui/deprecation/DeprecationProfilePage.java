package org.protege.editor.owl.ui.deprecation;

import org.protege.editor.core.ui.preferences.PreferencesLayoutPanel;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.deprecation.DeprecationProfile;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 29 Aug 2017
 */
public class DeprecationProfilePage extends AbstractOWLWizardPanel {

    public static final String ID = "DeprecationProfilePage";

    @Nonnull
    private final DeprecateEntityWizardState wizardState;

    private final JComboBox<DeprecationProfile> profilesCombo;

    public DeprecationProfilePage(@Nonnull OWLEditorKit owlEditorKit,
                                  @Nonnull DeprecateEntityWizardState wizardState,
                                  @Nonnull List<DeprecationProfile> deprecationProfiles) {
        super(ID, "Deprecation Profile", owlEditorKit);
        this.wizardState = wizardState;
        setInstructions("Please choose a deprecation profile");
        PreferencesLayoutPanel layoutPanel = new PreferencesLayoutPanel();
        layoutPanel.addGroup("Deprecation profile");
        profilesCombo = new JComboBox<>(deprecationProfiles.toArray(new DeprecationProfile [deprecationProfiles.size()]));
        profilesCombo.setRenderer(new DeprecationProfileRenderer());
        profilesCombo.addActionListener(e -> {

        });
        layoutPanel.addGroupComponent(profilesCombo);
        setContent(layoutPanel);
    }

    @Nullable
    @Override
    public Object getNextPanelDescriptor() {
        return DeprecationReasonPage.ID;
    }

    @Override
    public void aboutToHidePanel() {
        wizardState.setDeprecationProfile((DeprecationProfile) profilesCombo.getSelectedItem());
    }
}
