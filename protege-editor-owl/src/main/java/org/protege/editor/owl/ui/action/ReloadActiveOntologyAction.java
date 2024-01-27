package org.protege.editor.owl.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 13/03/15
 */
public class ReloadActiveOntologyAction extends ProtegeOWLAction {

    /**
     * Invoked when an action occurs.
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        OWLModelManager modelManager = getOWLModelManager();
        OWLOntology activeOntology = modelManager.getActiveOntology();
        if (getOWLModelManager().isDirty(activeOntology)) {
            int ret = JOptionPane.showConfirmDialog(getOWLWorkspace(), "Are you sure that you want to reload the active ontology?  You will lose any unsaved changes.", "Reload ontology?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(ret != JOptionPane.YES_OPTION) {
                return;
            }
        }
        try {
            modelManager.reload(activeOntology);
        } catch (OWLOntologyCreationException e1) {
            JOptionPane.showMessageDialog(getOWLWorkspace(), "There was an error reloading the active ontology.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * The initialise method is called at the start of a
     * plugin instance life cycle.
     * This method is called to give the plugin a chance
     * to initialise itself.  All plugin initialisation
     * should be done in this method rather than the plugin
     * constructor, since the initialisation might need to
     * occur at a point after plugin instance creation, and
     * a each plugin must have a zero argument constructor.
     */
    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }
}
