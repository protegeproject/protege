package org.protege.editor.owl.ui.view.dataproperty;

import org.protege.editor.owl.ui.view.AbstractOWLPropertyViewComponent;
import org.semanticweb.owlapi.model.OWLDataProperty;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 05-Jul-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class AbstractOWLDataPropertyViewComponent extends AbstractOWLPropertyViewComponent<OWLDataProperty> {

    /**
     * 
     */
    private static final long serialVersionUID = -8912688176976991148L;

    protected boolean isOWLDataPropertyView() {
        return true;
    }
}
