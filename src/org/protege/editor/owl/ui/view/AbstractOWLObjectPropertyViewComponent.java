package org.protege.editor.owl.ui.view;

import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLObjectProperty;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 25, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class AbstractOWLObjectPropertyViewComponent extends AbstractOWLSelectionViewComponent {

    protected boolean isOWLObjectPropertyView() {
        return true;
    }


    final protected OWLEntity updateView() {
        OWLObjectProperty selProp = updateView(getOWLWorkspace().getOWLSelectionModel().getLastSelectedObjectProperty());
        if (selProp != null) {
            updateRegisteredActions();
        }
        else {
            disableRegisteredActions();
        }
        return selProp;
    }


    protected abstract OWLObjectProperty updateView(OWLObjectProperty property);
}
