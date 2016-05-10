package org.protege.editor.owl.ui.view.objectproperty;

import org.protege.editor.owl.ui.view.AbstractOWLPropertyViewComponent;
import org.semanticweb.owlapi.model.OWLObjectProperty;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 25, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class AbstractOWLObjectPropertyViewComponent extends AbstractOWLPropertyViewComponent<OWLObjectProperty> {

    /**
     * 
     */
    private static final long serialVersionUID = -4876784124179907123L;

    protected boolean isOWLObjectPropertyView() {
        return true;
    }
}
