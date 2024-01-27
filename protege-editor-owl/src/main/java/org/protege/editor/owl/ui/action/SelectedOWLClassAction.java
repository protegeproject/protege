package org.protege.editor.owl.ui.action;

import java.util.Optional;

import javax.annotation.Nullable;

import org.protege.editor.owl.model.selection.OWLEntitySelectionModel;
import org.protege.editor.owl.model.selection.OWLSelectionModel;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.semanticweb.owlapi.model.OWLClass;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 03-Feb-2007<br><br>
 */
public abstract class SelectedOWLClassAction extends ProtegeOWLAction {

    private OWLSelectionModelListener listener;


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
    final public void initialise() throws Exception {
        listener = () -> updateState();
        getOWLWorkspace().getOWLSelectionModel().addListener(listener);
        updateState();
    }


    protected void updateState() {
        OWLSelectionModel selectionModel = getOWLWorkspace().getOWLSelectionModel();
        Optional<OWLClass> selectedEntity = new OWLEntitySelectionModel(selectionModel).getSelectedClass();
        setEnabled(selectedEntity.isPresent());
    }


    @Nullable
    protected OWLClass getOWLClass() {
        return getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass();
    }


    protected abstract void initialiseAction() throws Exception;


    /**
     * This method is called at the end of a plugin
     * life cycle, when the plugin needs to be removed
     * from the system.  Plugins should remove any listeners
     * that they setup and perform other cleanup, so that
     * the plugin can be garbage collected.
     */
    public void dispose() {
        if (listener != null) {
            getOWLWorkspace().getOWLSelectionModel().removeListener(listener);
        }
    }
}
