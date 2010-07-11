package org.protege.editor.owl.ui.inference;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 18-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ClassifyAction extends ProtegeOWLAction {
	private static final long serialVersionUID = -4602291778441065461L;


    private OWLModelManagerListener owlModelManagerListener;


    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        Frame owner = (Frame) (SwingUtilities.getAncestorOfClass(Frame.class, getOWLEditorKit().getWorkspace()));
        ChoosePreComputedInferencesDialog dialog = new  ChoosePreComputedInferencesDialog(owner, getOWLEditorKit().getModelManager().getReasonerPreferences());
        dialog.setVisible(true);
        if (dialog.isCancelled()) {
            ;
        }
        else if (!getOWLModelManager().getOWLReasonerManager().classifyAsynchronously(dialog.getPreCompute())) {
            Object[] options = {"OK", "Interrupt Current Classification"};
            int ret = JOptionPane.showOptionDialog(null,
                                                   "Classification already in progress.  New classification can't be started",
                                                   "Classification in progress",
                                                   JOptionPane.YES_NO_CANCEL_OPTION,
                                                   JOptionPane.QUESTION_MESSAGE,
                                                   null,
                                                   options,
                                                   options[0]);
            if (ret == 1) { 
                getOWLModelManager().getOWLReasonerManager().killCurrentClassification();
            }

        }
    }


    /**
     * This method is called at the end of a plugin
     * life cycle, when the plugin needs to be removed
     * from the system.  Plugins should remove any listeners
     * that they setup and perform other cleanup, so that
     * the plugin can be garbage collected.
     */
    public void dispose() {
        getOWLModelManager().removeListener(owlModelManagerListener);
    }


    /**
     * The initialise method is called at the start of a
     * plugin instance life cycle.
     * This method is called to give the plugin a chance
     * to intitialise itself.  All plugin initialisation
     * should be done in this method rather than the plugin
     * constructor, since the initialisation might need to
     * occur at a point after plugin instance creation, and
     * a each plugin must have a zero argument constructor.
     */
    public void initialise() throws Exception {
        owlModelManagerListener = new OWLModelManagerListener() {
            public void handleChange(OWLModelManagerChangeEvent event) {
                if (event.isType(EventType.ONTOLOGY_CLASSIFIED)) {
                    showClassificationResults();
                }
            }
        };
        getOWLModelManager().addListener(owlModelManagerListener);
    }


    /**
     * Brings the inferred hierarchy views to the font (if they are in the views pane),
     * and shows the classification results view.
     */
    private void showClassificationResults() {
        getOWLEditorKit().getWorkspace().getViewManager().bringViewToFront(
                "org.protege.editor.owl.InferredOWLClassHierarchy");
        getOWLEditorKit().getWorkspace().getViewManager().bringViewToFront(
                "org.protege.editor.owl.OWLInferredSuperClassHierarchy");
        //getOWLEditorKit().getWorkspace().showResultsView("org.protege.editor.owl.OWLReasonerResults", true, Workspace.BOTTOM_RESULTS_VIEW);
    }
}
