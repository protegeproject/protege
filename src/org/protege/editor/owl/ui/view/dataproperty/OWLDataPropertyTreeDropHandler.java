package org.protege.editor.owl.ui.view.dataproperty;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.view.OWLPropertyTreeDropHandler;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;


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
        return df.getOWLSubDataPropertyOfAxiom(child, parent);
    }
}
