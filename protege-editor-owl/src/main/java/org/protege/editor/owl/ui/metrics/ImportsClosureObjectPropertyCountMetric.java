package org.protege.editor.owl.ui.metrics;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 20-Feb-2007<br><br>
 */
public class ImportsClosureObjectPropertyCountMetric extends AbstractIntegerMetric {

    public ImportsClosureObjectPropertyCountMetric() {
        super("Object properties (imported)");
    }


    protected int getIntMetric() {
        Set<OWLObjectProperty> props = new HashSet<>();
        for (OWLOntology ont : getOWLModelManager().getActiveOntologies()) {
            props.addAll(ont.getObjectPropertiesInSignature());
        }
        return props.size();
    }
}
