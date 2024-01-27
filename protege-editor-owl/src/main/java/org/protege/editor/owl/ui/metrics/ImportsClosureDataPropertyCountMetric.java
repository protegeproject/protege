package org.protege.editor.owl.ui.metrics;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 20-Feb-2007<br><br>
 */
public class ImportsClosureDataPropertyCountMetric extends AbstractIntegerMetric {

    public ImportsClosureDataPropertyCountMetric() {
        super("Data properties (imported)");
    }


    protected int getIntMetric() {
        Set<OWLDataProperty> clses = new HashSet<>();
        for (OWLOntology ont : getOWLModelManager().getActiveOntologies()) {
            clses.addAll(ont.getDataPropertiesInSignature());
        }
        return clses.size();
    }
}
