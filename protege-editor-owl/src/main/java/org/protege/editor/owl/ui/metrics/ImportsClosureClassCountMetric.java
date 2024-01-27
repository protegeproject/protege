package org.protege.editor.owl.ui.metrics;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 20-Feb-2007<br><br>
 */
public class ImportsClosureClassCountMetric extends AbstractIntegerMetric {

    public ImportsClosureClassCountMetric() {
        super("Classes (imported)");
    }


    protected int getIntMetric() {
        Set<OWLClass> clses = new HashSet<>();
        for (OWLOntology ont : getOWLModelManager().getActiveOntologies()) {
            clses.addAll(ont.getClassesInSignature());
        }
        return clses.size();
    }
}
