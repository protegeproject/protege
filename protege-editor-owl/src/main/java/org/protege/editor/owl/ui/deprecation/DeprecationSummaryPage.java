package org.protege.editor.owl.ui.deprecation;

import static org.protege.editor.owl.ui.deprecation.DeprecationWizardEntityRenderer.renderer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;
import org.semanticweb.owlapi.model.OWLEntity;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 25 Aug 2017
 */
public class DeprecationSummaryPage extends AbstractOWLWizardPanel {

    public static final String ID = "DeprecationSummaryPage";

    @Nonnull
    private final OWLEntity entityToDeprecate;

    @Nonnull
    private final DeprecateEntityWizardState state;

    public DeprecationSummaryPage(@Nonnull OWLEditorKit owlEditorKit,
                                  @Nonnull OWLEntity entityToDeprecate,
                                  @Nonnull DeprecateEntityWizardState state) {
        super(ID, "Summary", owlEditorKit);
        this.entityToDeprecate = entityToDeprecate;
        this.state = state;
        StringBuilder instructions = new StringBuilder();
        instructions.append("Protégé will deprecate ");
        instructions.append(renderer(getOWLModelManager()).getHtmlRendering(entityToDeprecate));
        instructions.append(".");
        state.getReplacementEntity().ifPresent(repl -> {
            instructions.append("  This entity will be replaced with ");
            instructions.append(renderer(getOWLModelManager()).getHtmlRendering(repl));
            instructions.append(".");
        });
        instructions.append("\n\nPress Finish to close the wizard and perform the deprecation.");
        setInstructions(instructions.toString());
    }

    @Nullable
    @Override
    public Object getNextPanelDescriptor() {
        return FINISH;
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
}
