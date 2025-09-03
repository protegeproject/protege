package org.protege.editor.owl.ui.action;

/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


import org.protege.editor.owl.model.selection.OWLEntitySelectionModel;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 03-Feb-2007<br><br>
 */
public abstract class SelectedOWLObjectPropertyAction extends ProtegeOWLAction {

    private OWLSelectionModelListener listener = this::updateState;

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
        getOWLWorkspace().getOWLSelectionModel().addListener(listener);
        updateState();
        initialiseAction();
    }

    private void updateState() {
        OWLEntitySelectionModel entitySelectionModel = new OWLEntitySelectionModel(getOWLWorkspace().getOWLSelectionModel());
        setEnabled(entitySelectionModel.getSelectedObjectProperty().isPresent());
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
        getOWLWorkspace().getOWLSelectionModel().removeListener(listener);
    }
}
