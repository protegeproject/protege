package org.protege.editor.owl.ui.deprecation;

import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.deprecation.DeprecationProfile;
import org.protege.editor.owl.model.util.OboUtilities;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 24 Aug 2017
 */
public class DeprecateEntityWizard extends Wizard {

    private final DeprecateEntityWizardState wizardState = new DeprecateEntityWizardState();

    public DeprecateEntityWizard(@Nonnull Frame owner,
                                 @Nonnull OWLEditorKit editorKit,
                                 @Nonnull OWLEntity entityToDeprecate,
                                 @Nonnull DeprecationProfile strategy) {
        super(owner);
        setTitle(generateTitle(editorKit, entityToDeprecate));
        registerWizardPanel(DeprecationReasonPage.ID,
                            new DeprecationReasonPage(editorKit,
                                                      wizardState,
                                                      entityToDeprecate));
        registerWizardPanel(DeprecationReplacementEntityPage.ID,
                            new DeprecationReplacementEntityPage(editorKit,
                                                                 strategy,
                                                                 wizardState));
        registerWizardPanel(AlternateEntitiesPage.ID, new AlternateEntitiesPage(editorKit, wizardState));

        registerWizardPanel(DeprecationSummaryPage.ID,
                            new DeprecationSummaryPage(editorKit, entityToDeprecate, wizardState));

        setCurrentPanel(DeprecationReasonPage.ID);
    }

    private static String generateTitle(OWLEditorKit editorKit, OWLEntity entityToDeprecate) {
        String title = "Deprecate " + editorKit.getModelManager().getRendering(entityToDeprecate);
        title += OboUtilities.getOboIdFromIri(entityToDeprecate.getIRI()).map(oboId -> " (" + oboId + ")").orElse("");
        return title;
    }

    public DeprecateEntityWizardState getWizardState() {
        return wizardState;
    }
}
