package org.protege.editor.owl.ui.view;

import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLIndividual;


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

    protected boolean isOWLIndividualView() {
        return true;
    }


    final public void initialiseView() throws Exception {
        initialiseIndividualsView();
    }


    public OWLIndividual getSelectedOWLIndividual() {
        return getOWLWorkspace().getOWLSelectionModel().getLastSelectedIndividual();
    }


    final protected OWLEntity updateView() {
        OWLIndividual selIndividual = updateView(getSelectedOWLIndividual());
        if (selIndividual != null) {
            updateRegisteredActions();
        }
        else {
            disableRegisteredActions();
        }
        return selIndividual;
    }


    protected abstract OWLIndividual updateView(OWLIndividual selelectedIndividual);


    public abstract void initialiseIndividualsView() throws Exception;
}
