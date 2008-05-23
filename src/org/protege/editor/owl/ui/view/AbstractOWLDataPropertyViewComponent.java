package org.protege.editor.owl.ui.view;

import org.semanticweb.owl.model.OWLDataProperty;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 05-Jul-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class AbstractOWLDataPropertyViewComponent extends AbstractOWLPropertyViewComponent<OWLDataProperty> {

    protected boolean isOWLDataPropertyView() {
        return true;
    }
}
