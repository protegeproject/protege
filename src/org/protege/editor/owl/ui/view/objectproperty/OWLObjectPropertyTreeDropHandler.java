package org.protege.editor.owl.ui.view.objectproperty;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.view.OWLPropertyTreeDropHandler;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLSubPropertyAxiom;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 23-Jan-2007<br><br>
 */
public class OWLObjectPropertyTreeDropHandler extends OWLPropertyTreeDropHandler<OWLObjectProperty> {

    public OWLObjectPropertyTreeDropHandler(OWLModelManager owlModelManager) {
        super(owlModelManager);
    }


    protected OWLSubPropertyAxiom getAxiom(OWLDataFactory df, OWLObjectProperty child, OWLObjectProperty parent) {
        return df.getOWLSubObjectPropertyOfAxiom(child, parent);
    }
}
