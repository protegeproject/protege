package org.protege.editor.owl.ui.deprecation;

import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.model.deprecation.DeprecateEntityInfo;
import org.protege.editor.owl.model.deprecation.EntityDeprecator;
import org.protege.editor.owl.model.deprecation.OboDeprecationProfile;
import org.protege.editor.owl.model.entity.HomeOntologySupplier;
import org.protege.editor.owl.ui.action.SelectedOWLEntityAction;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.parameters.Imports;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 23 Aug 2017
 */
public class DeprecateSelectedEntityAction extends SelectedOWLEntityAction {

    @Override
    protected void actionPerformed(OWLEntity selectedEntity) {
        Frame frame = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, getWorkspace());
        OboDeprecationProfile strategy = new OboDeprecationProfile();
        DeprecateEntityWizard wizard = new DeprecateEntityWizard(frame,
                                                                 getOWLEditorKit(),
                                                                 selectedEntity,
                                                                 strategy);
        int ret = wizard.showModalDialog();
        if(ret == Wizard.FINISH_RETURN_CODE) {
            runDeprecation(selectedEntity, strategy, wizard);
        }

    }

    private void runDeprecation(OWLEntity selectedEntity,
                                OboDeprecationProfile strategy,
                                DeprecateEntityWizard wizard) {
        DeprecateEntityWizardState state = wizard.getWizardState();
        DeprecateEntityInfo<?> info = new DeprecateEntityInfo<>(
                selectedEntity,
                state.getReplacementEntity().orElse(null),
                state.getReasonForDeprecation(),
                new HashSet<>(state.getAlternateEntities())
        );
        EntityDeprecator<?> deprecator = new EntityDeprecator<>(info,
                                                                strategy,
                                                                getOWLModelManager().getActiveOntologies(),
                                                                new HomeOntologySupplier(),
                                                                getOWLDataFactory());
        getOWLModelManager().applyChanges(deprecator.getChanges());
        long referenceCount = getOWLModelManager().getActiveOntology().getReferencingAxioms(selectedEntity, Imports.INCLUDED).stream()
                                                  .filter(OWLAxiom::isLogicalAxiom)
                                                  .count();
        // Ensure the deprecated entity is selected
        getOWLWorkspace().getOWLSelectionModel().setSelectedEntity(selectedEntity);
        if(referenceCount != 0) {
            JOptionPane.showMessageDialog(getOWLWorkspace(),
                                          "<html><body>" +
                                                  "There are " + referenceCount + " logical axioms that refer to the entity that " +
                                                  "has just been deprecated/obsoleted.<br>" +
                                                  "You should use the usage view " +
                                                  "to examine and replace axioms that reference the entity." +
                                                  "</body></html>",
                                          "Deprecated Entity has " + referenceCount + " usages",
                                          JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    protected void disposeAction() throws Exception {

    }
}
