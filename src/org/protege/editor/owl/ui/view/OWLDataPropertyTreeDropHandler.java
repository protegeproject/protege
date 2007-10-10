package org.protege.editor.owl.ui.view;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataProperty;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 23-Jan-2007<br><br>
 */
public class OWLDataPropertyTreeDropHandler extends OWLPropertyTreeDropHandler<OWLDataProperty> {

    public OWLDataPropertyTreeDropHandler(OWLModelManager owlModelManager) {
        super(owlModelManager);
    }


    protected OWLAxiom getAxiom(OWLDataFactory df, OWLDataProperty child, OWLDataProperty parent) {
        return df.getOWLSubDataPropertyAxiom(child, parent);
    }
}
