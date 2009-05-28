package org.protege.editor.owl.model.refactor.ontology;

import org.apache.log4j.Logger;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLAxiomVisitorAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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

    private OWLOntology targetOntology;


    public OntologyMerger(OWLOntologyManager owlOntologyManager, Set<OWLOntology> ontologies, OWLOntology targetOntology) {
        this.ontologies = new HashSet<OWLOntology>(ontologies);
        this.owlOntologyManager = owlOntologyManager;
        this.targetOntology = targetOntology;
    }


    public void mergeOntologies() {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for (OWLOntology ont : ontologies) {
            if (!ont.equals(targetOntology)){

                // move the axioms
                for (OWLAxiom ax : ont.getAxioms()) {
                    changes.add(new AddAxiom(targetOntology, ax));
                }

                // move ontology annotations
                for (OWLAnnotation annot : ont.getAnnotations()){
                    changes.add(new AddOntologyAnnotation(targetOntology, annot));
                }

                // move ontology imports
                for (OWLImportsDeclaration decl : ont.getImportsDeclarations()){
                    if (!decl.getURI().equals(targetOntology.getURI())){
                        changes.add(new AddImport(targetOntology, decl));
                    }
                    else{
                        logger.warn("Merge: ignoring import declaration for URI " + targetOntology.getURI() +
                                    " (would result in targetOntology importing itself).");
                    }
                }
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
