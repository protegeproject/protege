package org.protege.editor.owl.ui.metrics;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLOntology;


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
