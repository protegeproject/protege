package org.protege.editor.owl.ui.deprecation;

import com.google.common.base.Optional;
import org.protege.editor.core.ui.preferences.PreferencesLayoutPanel;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.deprecation.DeprecationProfile;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;
import org.semanticweb.owlapi.model.IRI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
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

    private final JLabel descriptionLabel  = new JLabel();

    private final List<DeprecationProfile> deprecationProfiles = new ArrayList<>();

    public DeprecationProfilePage(@Nonnull OWLEditorKit owlEditorKit,
                                  @Nonnull DeprecateEntityWizardState wizardState,
                                  @Nonnull List<DeprecationProfile> deprecationProfiles) {
        super(ID, "Deprecation Profile", owlEditorKit);
        this.deprecationProfiles.addAll(deprecationProfiles);
        this.wizardState = wizardState;
        setInstructions("<b>Please choose a deprecation profile</b>");
        profilesCombo = new JComboBox<>(deprecationProfiles.toArray(new DeprecationProfile [deprecationProfiles.size()]));
        profilesCombo.setRenderer(new DeprecationProfileRenderer());
        profilesCombo.addActionListener(e -> setDescription());
        JPanel content = new JPanel(new BorderLayout(7, 7));
        content.add(profilesCombo, BorderLayout.NORTH);
        content.add(descriptionLabel, BorderLayout.SOUTH);
        descriptionLabel.setBorder(BorderFactory.createEmptyBorder(0, 7, 0, 0));
        JPanel holder = new JPanel(new BorderLayout());
        holder.add(content, BorderLayout.NORTH);
        setContent(holder);
    }

    private void setActiveSelected() {
        deprecationProfiles.forEach(profile -> {
            profile.getActivatedBy().ifPresent(activatedByIri -> {
                Optional<IRI> ontologyIRI = getOWLModelManager().getActiveOntology().getOntologyID().getOntologyIRI();
                if(ontologyIRI.isPresent()) {
                    if(ontologyIRI.get().equals(activatedByIri)) {
                        profilesCombo.setSelectedItem(profile);
                        setDescription();
                    }
                }
            });
        });
        setDescription();
    }

    private void setDescription() {
        String description = ((DeprecationProfile) profilesCombo.getSelectedItem()).getDescription();
        descriptionLabel.setText(String.format("<html><body>%s</body></html>",
                                               description.replace("\n", "<br>")));
    }

    @Nullable
    @Override
    public Object getNextPanelDescriptor() {
        return DeprecationReasonPage.ID;
    }

    @Override
    public void aboutToDisplayPanel() {
        setActiveSelected();
    }

    @Override
    public void aboutToHidePanel() {
        wizardState.setDeprecationProfile((DeprecationProfile) profilesCombo.getSelectedItem());
    }
}
