package org.protege.editor.owl.ui.deprecation;

import java.awt.BorderLayout;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.deprecation.DeprecationProfile;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;
import org.semanticweb.owlapi.model.OWLEntity;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 24 Aug 2017
 */
public class DeprecationLogicalAxiomsTreatmentPage extends AbstractOWLWizardPanel {

    public static final String ID = "DeprecationLogicalAxiomsTreatmentPage";

    @Nonnull
    private final OWLEntity entityToDeprecate;

    @Nonnull
    private final DeprecationProfile strategy;

    @Nonnull
    private final DeprecateEntityWizardState state;

    public DeprecationLogicalAxiomsTreatmentPage(@Nonnull OWLEditorKit owlEditorKit,
                                                 @Nonnull OWLEntity entityToDeprecate,
                                                 @Nonnull DeprecationProfile strategy,
                                                 @Nonnull DeprecateEntityWizardState state) {
        super(ID, "Delete Logical Axioms?", owlEditorKit);
        this.entityToDeprecate = entityToDeprecate;
        this.strategy = strategy;
        this.state = state;
        setInstructions("Do you want to delete the logical axioms that define the deprecated entity?");
        Box box = new Box(BoxLayout.Y_AXIS);
        JRadioButton remove = new JRadioButton("Delete logical defining axioms (recommended)", true);
        box.add(remove);
        JRadioButton doNotRemove = new JRadioButton("Do not delete logical defining axioms", true);
        box.add(doNotRemove);
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(remove);
        buttonGroup.add(doNotRemove);
        JPanel holder = new JPanel(new BorderLayout());
        holder.add(box, BorderLayout.NORTH);
        setContent(holder);
    }

    @Nullable
    @Override
    public Object getBackPanelDescriptor() {
        if (state.getReplacementEntity().isPresent()) {
            return DeprecationReplacementEntityPage.ID;
        }
        else {
            return AlternateEntitiesPage.ID;
        }
    }

    @Nullable
    @Override
    public Object getNextPanelDescriptor() {
        return DeprecationSummaryPage.ID;
    }
}
