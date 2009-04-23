package org.protege.editor.owl.ui.frame;

import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLLiteral;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 15-Feb-2007<br><br>
 */
public class OWLDataPropertyConstantPair {

    private OWLDataProperty property;

    private OWLLiteral constant;


    public OWLDataPropertyConstantPair(OWLDataProperty property, OWLLiteral constant) {
        this.property = property;
        this.constant = constant;
    }


    public OWLDataProperty getProperty() {
        return property;
    }


    public OWLLiteral getConstant() {
        return constant;
    }
}
