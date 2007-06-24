package org.protege.editor.owl.model.refactor.ontology;

import java.util.Collections;

import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 01-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OntologyMigrator {

    private OWLOntology existingOntology;

    private OWLOntology newOntology;

    private OWLOntologyManager owlOntologyManager;


    public OntologyMigrator(OWLOntologyManager owlOntologyManager, OWLOntology existingOntology,
                            OWLOntology newOntology) {
        this.owlOntologyManager = owlOntologyManager;
        this.existingOntology = existingOntology;
        this.newOntology = newOntology;
    }


    public void performMigration() {
        OntologyMerger merger = new OntologyMerger(owlOntologyManager,
                                                   Collections.singleton(existingOntology),
                                                   newOntology);
        merger.mergeOntologies();
    }
}
