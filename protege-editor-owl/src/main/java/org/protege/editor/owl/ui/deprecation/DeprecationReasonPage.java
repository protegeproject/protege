package org.protege.editor.owl.ui.deprecation;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 24 Aug 2017
 */
public class DeprecationReasonPage extends AbstractOWLWizardPanel {

    public static final String ID = "DeprecationReason";

    private final JTextArea reasonTextArea = new JTextArea();

    @Nonnull
    private final DeprecateEntityWizardState wizardState;

    public DeprecationReasonPage(@Nonnull OWLEditorKit editorKit,
                                 @Nonnull DeprecateEntityWizardState wizardState,
                                 @Nonnull OWLEntity entityToDeprecate) {
        super(ID, "Reason for deprecation", editorKit);
        this.wizardState = checkNotNull(wizardState);
        JPanel contentPanel = new JPanel(new BorderLayout(7, 7));
        setContent(contentPanel);
        setInstructions(new DeprecationWizardEntityRenderer(getOWLModelManager()).getHtmlRendering(entityToDeprecate) + "\n\n" +
                                "Please specify a reason that explains why this entity is to be deprecated.\n\n" +
                                "The deprecated entity will be annotated with this reason so that consumers of this " +
                                "ontology understand why the entity was deprecated.");
        contentPanel.add(reasonTextArea, BorderLayout.CENTER);
    }

    public String getReasonForDeprecation() {
        return reasonTextArea.getText().trim();
    }

    @Override
    public void aboutToHidePanel() {
        wizardState.setReasonForDeprecation(getReasonForDeprecation());
    }

    @Nullable
    @Override
    public Object getBackPanelDescriptor() {
        return DeprecationProfilePage.ID;
    }

    @Nullable
    @Override
    public Object getNextPanelDescriptor() {
        if (wizardState.getDeprecationProfile().map(profile -> profile.getDeprecationCode().isPresent()).orElse(false)) {
            return DeprecationCodePage.ID;
        }
        else {
            return DeprecationReplacementEntityPage.ID;
        }
    }

    @Override
    public void displayingPanel() {
        super.displayingPanel();
        reasonTextArea.requestFocus();
    }
}
