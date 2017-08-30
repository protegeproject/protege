package org.protege.editor.owl.ui.deprecation;

import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.model.deprecation.DeprecateEntityInfo;
import org.protege.editor.owl.model.deprecation.DeprecationProfile;
import org.protege.editor.owl.model.deprecation.DeprecationProfileLoader;
import org.protege.editor.owl.model.deprecation.EntityDeprecator;
import org.protege.editor.owl.model.entity.HomeOntologySupplier;
import org.protege.editor.owl.ui.action.SelectedOWLEntityAction;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.util.HashSet;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 23 Aug 2017
 */
public class DeprecateSelectedEntityAction extends SelectedOWLEntityAction {

    private static final Logger logger = LoggerFactory.getLogger(DeprecateSelectedEntityAction.class);

    @Override
    protected void actionPerformed(OWLEntity selectedEntity) {
        Frame frame = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, getWorkspace());
        DeprecationProfileLoader profileLoader = new DeprecationProfileLoader();
        try {
            List<DeprecationProfile> profiles = profileLoader.loadProfiles();

            DeprecateEntityWizard wizard = new DeprecateEntityWizard(frame,
                                                                     getOWLEditorKit(),
                                                                     selectedEntity,
                                                                     profiles);
            int ret = wizard.showModalDialog();
            if(ret == Wizard.FINISH_RETURN_CODE) {
                runDeprecation(selectedEntity, wizard);
            }
        } catch (IOException e) {
            logger.error("Unabled to load deprecation profiles: {}", e.getMessage(), e);
        }

    }

    private void runDeprecation(OWLEntity selectedEntity,
                                DeprecateEntityWizard wizard) {
        DeprecateEntityWizardState state = wizard.getWizardState();
        DeprecateEntityInfo<?> info = new DeprecateEntityInfo<>(
                selectedEntity,
                state.getReplacementEntity().orElse(null),
                state.getReasonForDeprecation(),
                new HashSet<>(state.getAlternateEntities()),
                state.getDeprecationCode().orElse(null));
        EntityDeprecator<?> deprecator = new EntityDeprecator<>(info,
                                                                wizard.getWizardState().getDeprecationProfile().get(),
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
