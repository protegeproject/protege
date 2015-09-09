package org.protege.editor.owl.ui.view.individual;

import org.protege.editor.owl.ui.view.AbstractOWLSelectionViewComponent;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 07-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class AbstractOWLIndividualViewComponent extends AbstractOWLSelectionViewComponent {

    /**
     * 
     */
    private static final long serialVersionUID = 4924001251482699052L;


    protected boolean isOWLIndividualView() {
        return true;
    }


    final public void initialiseView() throws Exception {
        initialiseIndividualsView();
    }


    public OWLNamedIndividual getSelectedOWLIndividual() {
        return getOWLWorkspace().getOWLSelectionModel().getLastSelectedIndividual();
    }


    final protected OWLEntity updateView() {
        OWLNamedIndividual selIndividual = updateView(getSelectedOWLIndividual());
        if (selIndividual != null) {
            updateRegisteredActions();
        }
        else {
            disableRegisteredActions();
        }
        return selIndividual;
    }


    public abstract OWLNamedIndividual updateView(OWLNamedIndividual individual);


    public abstract void initialiseIndividualsView() throws Exception;
}
