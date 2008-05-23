package org.protege.editor.owl.ui.view;

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
public abstract class AbstractOWLObjectPropertyViewComponent extends AbstractOWLPropertyViewComponent<OWLObjectProperty> {

    protected boolean isOWLObjectPropertyView() {
        return true;
    }
}
