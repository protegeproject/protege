package org.protege.editor.owl.ui.inference;

import java.awt.event.ActionEvent;
import java.util.Set;

import javax.swing.JOptionPane;

import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.inference.ReasonerPreferences;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;
import org.semanticweb.owlapi.reasoner.InferenceType;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 18-May-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PrecomputeAction extends ProtegeOWLAction {
	private static final long serialVersionUID = -4602291778441065461L;

    private OWLModelManagerListener owlModelManagerListener;
        
    public PrecomputeAction() {
    }


    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        ReasonerPreferences  preferences = getOWLModelManager().getOWLReasonerManager().getReasonerPreferences();
        Set<InferenceType> precompute;
        precompute = preferences.getPrecomputedInferences();
        if (!getOWLModelManager().getOWLReasonerManager().classifyAsynchronously(precompute)) {
            Object[] options = {"OK", "Interrupt Current Reasoning Task"};
            int ret = JOptionPane.showOptionDialog(null,
                                                   "Reasoner initialization still in progress.  New initialization can't be started",
                                                   "Reasoner initialization in progress",
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
        owlModelManagerListener = event -> {
            if (event.isType(EventType.ONTOLOGY_CLASSIFIED)) {
                showClassificationResults();
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
