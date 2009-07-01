package org.protege.editor.owl.ui.metrics;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.HashSet;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 20-Feb-2007<br><br>
 */
public class ImportsClosureIndividualCountMetric extends AbstractIntegerMetric {

    public ImportsClosureIndividualCountMetric() {
        super("Individuals (imported)");
    }


    protected int getIntMetric() {
        Set<OWLIndividual> clses = new HashSet<OWLIndividual>();
        for (OWLOntology ont : getOWLModelManager().getActiveOntologies()) {
            clses.addAll(ont.getReferencedIndividuals());
        }
        return clses.size();
    }
}
