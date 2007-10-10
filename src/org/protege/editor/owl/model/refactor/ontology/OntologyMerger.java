package org.protege.editor.owl.model.refactor.ontology;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyManager;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 22-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OntologyMerger {

    private static final Logger logger = Logger.getLogger(OntologyMerger.class);

    private OWLOntologyManager owlOntologyManager;

    private Set<OWLOntology> ontologies;

    private OWLOntology ontology;


    public OntologyMerger(OWLOntologyManager owlOntologyManager, Set<OWLOntology> ontologies, OWLOntology ontology) {
        this.ontologies = new HashSet<OWLOntology>(ontologies);
        this.owlOntologyManager = owlOntologyManager;
        this.ontology = ontology;
    }


    public void mergeOntologies() {
        // Such a breeze with the new API! :)
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for (OWLOntology ont : ontologies) {
            for (OWLAxiom ax : ont.getAxioms()) {
                changes.add(new AddAxiom(ontology, ax));
            }
        }
        try {
            owlOntologyManager.applyChanges(changes);
        }
        catch (OWLOntologyChangeException e) {
            e.printStackTrace();
        }
    }
}
