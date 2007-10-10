package org.protege.editor.owl.ui.view;

import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLEntity;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 05-Jul-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class AbstractOWLDataPropertyViewComponent extends AbstractOWLSelectionViewComponent {

    public void disposeView() {
    }


    final protected OWLEntity updateView() {
        OWLDataProperty selProp = updateView(getOWLWorkspace().getOWLSelectionModel().getLastSelectedDataProperty());
        if (selProp != null) {
            updateRegisteredActions();
        }
        else {
            disableRegisteredActions();
        }
        return selProp;
    }


    protected abstract OWLDataProperty updateView(OWLDataProperty property);


    protected boolean isOWLDataPropertyView() {
        return true;
    }
}
