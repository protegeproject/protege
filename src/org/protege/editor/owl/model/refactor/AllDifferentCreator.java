package org.protege.editor.owl.model.refactor;

import java.util.List;
import java.util.Set;

import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChange;


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

    private Set<OWLOntology> ontologies;


    public AllDifferentCreator(Set<OWLOntology> ontologies) {
        this.ontologies = ontologies;
    }


    public List<OWLOntologyChange> getChanges() {
        throw new RuntimeException("NOT IMPLEMENTED");
//        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
//        for(OWLOntology ont : ontologies) {
//            for(OWLIndividualAxiom axiom : ont.getIndividualAxioms()) {
//                if(axiom instanceof OWLDifferentIndividualsAxiom) {
//                    changes.add(new RemoveIndividualAxiom(ont, axiom, null));
//                }
//            }
//            changes.add(new AddIndividualAxiom(ont,
//                                               ont.getOWLDataFactory().getOWLDifferentIndividualsAxiom(
//                                                       ont.getIndividuals()
//                                               ), null));
//        }
//        return changes;
    }
}
