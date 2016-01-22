package org.protege.editor.owl.model;

import org.protege.editor.core.log.LogBanner;
import org.semanticweb.owlapi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 21/01/16
 */
public class OntologyReloader {

    private static final Logger logger = LoggerFactory.getLogger(OntologyReloader.class);

    private final OWLOntologyManager manager;

    public OntologyReloader(OWLOntologyManager manager) {
        this.manager = manager;
    }

    /**
     * Reloads the specified ontology.  Either the ontology is successfully reloaded or it is left intact.
     * @param ont The ontology to be reloaded.
     * @throws OWLOntologyCreationException
     */
    public void reload(final OWLOntology ont) throws OWLOntologyCreationException {
        logger.info(LogBanner.start("Reloading ontology"));
        logger.info("Reloading ontology: {}", ont.getOntologyID());
        IRI ontologyDocumentIRI = manager.getOntologyDocumentIRI(ont);
        IRI tempDocumentIRI = IRI.create(ontologyDocumentIRI.toString() + ".tmp");
        // Set a temporary Id - anonymous - so that the ontology can be reloaded in the same
        // manager (so that imports don't have to be reloaded)
        manager.applyChange(new SetOntologyID(ont, new OWLOntologyID()));
        // Set a temporary document IRI so that the ontology isn't refound by its document IRI
        manager.setOntologyDocumentIRI(ont, tempDocumentIRI);
        try {
            // Load the ontology as a fresh ontology
            OWLOntology reloadedOnt = manager.loadOntologyFromOntologyDocument(ontologyDocumentIRI);
            // We don't need the ontology in the manager now.
            manager.removeOntology(reloadedOnt);
            // Compute a diff between the original and the reloaded ontology
            List<OWLOntologyChange> changes = new ArrayList<>();
            generateChangesToTransferContent(reloadedOnt, ont, changes);
            logger.info("    Applying {} change(s) to patch ontology to reloaded ontology", changes.size());
            manager.applyChanges(changes);
        } catch (Throwable t) {
            if (t instanceof OWLOntologyCreationException) {
                throw (OWLOntologyCreationException) t;
            }
            else {
                throw new OWLOntologyCreationException(t);
            }
        }
        finally {
            // Restore DocumentIRI
            manager.setOntologyDocumentIRI(ont, ontologyDocumentIRI);
            logger.info(LogBanner.end());
        }
    }

    /**
     * Generates
     * @param from The ontology that should essentially be the final ontology
     * @param to The ontology to which changes should be applied to make it the same (in terms of id, annotations,
     *           imports and axioms) as the from ontology
     * @param changeList A list that will be filled with changes
     */
    private void generateChangesToTransferContent(OWLOntology from, OWLOntology to, List<OWLOntologyChange> changeList) {
        for(OWLImportsDeclaration decl : from.getImportsDeclarations()) {
            if (!to.getImportsDeclarations().contains(decl)) {
                changeList.add(new AddImport(to, decl));
            }
        }
        for(OWLImportsDeclaration decl : to.getImportsDeclarations()) {
            if (!from.getImportsDeclarations().contains(decl)) {
                changeList.add(new RemoveImport(to, decl));
            }
        }
        for(OWLAnnotation annotation : from.getAnnotations()) {
            if (!to.getAnnotations().contains(annotation)) {
                changeList.add(new AddOntologyAnnotation(to, annotation));
            }
        }
        for(OWLAnnotation annotation : to.getAnnotations()) {
            if (!from.getAnnotations().contains(annotation)) {
                changeList.add(new RemoveOntologyAnnotation(to, annotation));
            }
        }
        for(OWLAxiom ax : from.getAxioms()) {
            if (!to.containsAxiom(ax)) {
                changeList.add(new AddAxiom(to, ax));
            }
        }
        for(OWLAxiom ax : to.getAxioms()) {
            if (!from.containsAxiom(ax)) {
                changeList.add(new RemoveAxiom(to, ax));
            }
        }
        if(!from.getOntologyID().equals(to.getOntologyID())) {
            changeList.add(new SetOntologyID(to, from.getOntologyID()));
        }
    }
}
