package org.protege.editor.owl.ui.deprecation;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.deprecation.DeprecationProfile;
import org.protege.editor.owl.model.util.OboUtilities;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;
import org.protege.editor.owl.ui.find.EntityFinderField;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 24 Aug 2017
 */
public class DeprecationReplacementEntityPage extends AbstractOWLWizardPanel {

    public static final String ID = "DeprecationReplacementEntityPage";

    @Nonnull
    private final DeprecateEntityWizardState wizardState;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<OWLEntity> selectedEntity = Optional.empty();

    private JLabel chosenReplacementLabel = new JLabel();

    private final EntityFinderField entityFinderField;

    public DeprecationReplacementEntityPage(@Nonnull OWLEditorKit editorKit,
                                            @Nonnull DeprecateEntityWizardState wizardState) {
        super(ID, "Direct replacement entity", editorKit);
        this.wizardState = checkNotNull(wizardState);
        setInstructions("<b>Please specify an entity that should be used as a direct replacement for the deprecated entity.</b>\n\n" +
                                "Specifying a replacement entity will replace usages of the deprecated entity with the replacement entity. An " +
                                "annotation will also be added to \"point\" to this replacement entity.\n\n" +
                                "Use the search field below to search for a replacement entity.  If " +
                                "you do not want to specify a replacement entity leave the field empty and press Continue."
                                );
        JPanel content = new JPanel(new BorderLayout(7, 7));
        entityFinderField = new EntityFinderField(content, editorKit);
        entityFinderField.setEntityFoundHandler(this::setReplacementEntity);
        entityFinderField.setSearchStartedHandler(this::clearReplacementEntity);
        content.add(entityFinderField, BorderLayout.NORTH);
        content.add(chosenReplacementLabel, BorderLayout.SOUTH);
        JPanel contentHolder = new JPanel(new BorderLayout());
        contentHolder.add(content, BorderLayout.NORTH);
        setContent(contentHolder);
    }

    private void clearReplacementEntity() {
        selectedEntity = Optional.empty();
        chosenReplacementLabel.setText("");
    }

    private void setReplacementEntity(@Nonnull OWLEntity entity) {
        OWLEditorKit editorKit = getOWLEditorKit();
        String rendering = editorKit.getModelManager().getRendering(entity);
        entityFinderField.setText(rendering);
        selectedEntity = Optional.of(entity);
        chosenReplacementLabel.setText("<html><body>" +
                                               "The deprecated entity will be replaced with <span style=\"font-weight: bold;\">" + rendering + "</span>" + OboUtilities.getOboIdFromIri(entity.getIRI()).map(id -> " (" + id + ")").orElse(""));
    }

    public Optional<OWLEntity> getReplacementEntity() {
        return selectedEntity;
    }

    @Override
    public void aboutToHidePanel() {
        wizardState.setReplacementEntity(selectedEntity.orElse(null));
    }

    @Nullable
    @Override
    public Object getBackPanelDescriptor() {
        return DeprecationReasonPage.ID;
    }

    @Nullable
    @Override
    public Object getNextPanelDescriptor() {
        Optional<DeprecationProfile> profile = wizardState.getDeprecationProfile();
        if(profile.isPresent() && profile.get().getAlternateEntityAnnotationPropertyIri().isPresent()
                && !selectedEntity.isPresent()) {
            return AlternateEntitiesPage.ID;
        }
        else {
            return DeprecationSummaryPage.ID;
        }
    }

    @Override
    public void displayingPanel() {
        super.displayingPanel();
        entityFinderField.requestFocus();
    }
}
