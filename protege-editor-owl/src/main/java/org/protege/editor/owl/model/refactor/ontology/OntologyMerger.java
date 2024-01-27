package org.protege.editor.owl.model.refactor.ontology;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.protege.editor.core.ui.error.ErrorLogPanel;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.AddOntologyAnnotation;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 22-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OntologyMerger {

    private static final Logger logger = LoggerFactory.getLogger(OntologyMerger.class);

    private OWLOntologyManager owlOntologyManager;

    private Set<OWLOntology> ontologies;

    private OWLOntology targetOntology;


    public OntologyMerger(OWLOntologyManager owlOntologyManager, Set<OWLOntology> ontologies, OWLOntology targetOntology) {
        this.ontologies = new HashSet<>(ontologies);
        this.owlOntologyManager = owlOntologyManager;
        this.targetOntology = targetOntology;
    }


    public void mergeOntologies() {
        List<OWLOntologyChange> changes = new ArrayList<>();
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

                if (!targetOntology.getOntologyID().isAnonymous()){
                    // move ontology imports
                    for (OWLImportsDeclaration decl : ont.getImportsDeclarations()){
                    	if (ontologies.contains(ont.getOWLOntologyManager().getImportedOntology(decl))) {
                    		continue;
                    	}
                        Optional<IRI> defaultDocumentIRI = targetOntology.getOntologyID().getDefaultDocumentIRI();
                        if (defaultDocumentIRI.isPresent() && !decl.getIRI().equals(defaultDocumentIRI.get())){
                            changes.add(new AddImport(targetOntology, decl));
                        }
                        else{
                            logger.warn("Merge: ignoring import declaration for ontology " + targetOntology.getOntologyID() +
                                        " (would result in target ontology importing itself).");
                        }
                    }
                }
            }
        }
        try {
            owlOntologyManager.applyChanges(changes);
        }
        catch (OWLOntologyChangeException e) {
            ErrorLogPanel.showErrorDialog(e);
        }
    }
}
