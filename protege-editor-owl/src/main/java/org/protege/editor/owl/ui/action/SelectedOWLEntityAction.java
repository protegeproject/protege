package org.protege.editor.owl.ui.action;

import java.awt.event.ActionEvent;

import org.protege.editor.owl.model.selection.OWLSelectionModel;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.semanticweb.owlapi.model.OWLEntity;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 22-Feb-2007<br><br>

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
        listener = () -> updateState();
        updateState();
        getOWLWorkspace().getOWLSelectionModel().addListener(listener);
    }

    public void performExtraInitialisation() {

    }


    private void updateState() {
        OWLSelectionModel selectionModel = getOWLWorkspace().getOWLSelectionModel();
        setEnabled(selectionModel.getSelectedObject() instanceof OWLEntity
                           || (selectionModel.getSelectedObject() != null && selectionModel.getSelectedEntity() != null));
    }


    final public void dispose() throws Exception {
        getOWLWorkspace().getOWLSelectionModel().removeListener(listener);
        disposeAction();
    }


    protected abstract void disposeAction() throws Exception;
}
