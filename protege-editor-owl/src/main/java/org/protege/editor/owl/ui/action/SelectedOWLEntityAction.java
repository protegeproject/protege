package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.semanticweb.owlapi.model.OWLEntity;

import java.awt.event.ActionEvent;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 22-Feb-2007<br><br>
 * <p/>
 * A base class for actions which perform an action based on the selected entity.
 */
public abstract class SelectedOWLEntityAction extends ProtegeOWLAction {

    private OWLSelectionModelListener listener;

    final public void actionPerformed(ActionEvent e) {
        actionPerformed(getSelectedEntity());
    }


    protected OWLEntity getSelectedEntity() {
        return getOWLWorkspace().getOWLSelectionModel().getSelectedEntity();
    }


    protected abstract void actionPerformed(OWLEntity selectedEntity);


    final public void initialise() throws Exception {
        listener = new OWLSelectionModelListener() {
            public void selectionChanged() throws Exception {
                updateState();
            }
        };
        updateState();
        getOWLWorkspace().getOWLSelectionModel().addListener(listener);
    }


    private void updateState() {
        setEnabled(getSelectedEntity() != null);
    }


    final public void dispose() throws Exception {
        getOWLWorkspace().getOWLSelectionModel().removeListener(listener);
        disposeAction();
    }


    protected abstract void disposeAction() throws Exception;
}
