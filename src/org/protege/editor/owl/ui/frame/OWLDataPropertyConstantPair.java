package org.protege.editor.owl.ui.frame;

import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataProperty;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 15-Feb-2007<br><br>
 */
public class OWLDataPropertyConstantPair {

    private OWLDataProperty property;

    private OWLConstant constant;


    public OWLDataPropertyConstantPair(OWLDataProperty property, OWLConstant constant) {
        this.property = property;
        this.constant = constant;
    }


    public OWLDataProperty getProperty() {
        return property;
    }


    public OWLConstant getConstant() {
        return constant;
    }
}
