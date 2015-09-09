package org.protege.editor.owl.ui.view.datatype;

import org.protege.editor.owl.ui.view.AbstractOWLSelectionViewComponent;
import org.semanticweb.owlapi.model.OWLDatatype;
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
 * Date: Jun 5, 2009<br><br>
 */
public abstract class AbstractOWLDataTypeViewComponent extends AbstractOWLSelectionViewComponent {


    /**
     * 
     */
    private static final long serialVersionUID = 2868427210452429514L;


    protected boolean isOWLDatatypeView() {
        return true;
    }


    protected final OWLObject updateView() {
        OWLDatatype dt = getOWLWorkspace().getOWLSelectionModel().getLastSelectedDatatype();
        updateView(dt);
        return dt;
    }


    protected abstract OWLDatatype updateView(OWLDatatype dt);
}
