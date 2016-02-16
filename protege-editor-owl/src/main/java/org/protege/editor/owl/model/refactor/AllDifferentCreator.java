package org.protege.editor.owl.model.refactor;

import org.semanticweb.owlapi.model.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 01-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class AllDifferentCreator {

    private OWLOntology ont;

    private Set<OWLOntology> ontologies;

    private OWLDataFactory dataFactory;

    public AllDifferentCreator(OWLDataFactory dataFactory, OWLOntology ont, Set<OWLOntology> ontologies) {
        this.ont =  ont;
        this.ontologies = ontologies;
        this.dataFactory = dataFactory;
    }


    public List<OWLOntologyChange> getChanges() {
        Set<OWLIndividual> individuals = new HashSet<>();
        for(OWLOntology ont : ontologies) {
            individuals.addAll(ont.getIndividualsInSignature());
        }
        List<OWLOntologyChange> changes = new ArrayList<>();
        if(!individuals.isEmpty()) {
            changes.add(new AddAxiom(ont, dataFactory.getOWLDifferentIndividualsAxiom(individuals)));
        }
        return changes;
    }
}
