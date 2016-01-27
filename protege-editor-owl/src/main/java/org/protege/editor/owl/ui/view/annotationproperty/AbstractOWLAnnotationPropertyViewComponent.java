package org.protege.editor.owl.ui.view.annotationproperty;

import org.protege.editor.owl.ui.view.AbstractOWLSelectionViewComponent;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLObject;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Apr 15, 2009<br><br>
 */
public abstract class AbstractOWLAnnotationPropertyViewComponent extends AbstractOWLSelectionViewComponent {



    protected boolean isOWLAnnotationPropertyView() {
        return true;
    }


    protected OWLObject updateView() {
        OWLAnnotationProperty selProp = updateView(getOWLWorkspace().getOWLSelectionModel().getLastSelectedAnnotationProperty());
        if (selProp != null) {
            updateRegisteredActions();
        }
        else {
            disableRegisteredActions();
        }
        return selProp;
    }


    protected abstract OWLAnnotationProperty updateView(OWLAnnotationProperty property);
}
