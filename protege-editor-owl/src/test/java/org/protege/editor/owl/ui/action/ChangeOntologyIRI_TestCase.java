package org.protege.editor.owl.ui.action;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveImport;
import org.semanticweb.owlapi.model.SetOntologyID;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class ChangeOntologyIRI_TestCase {

    private static final IRI IMPORTING_ONTOLOGY_IRI = IRI.create("http://example.org/importing");

    @Test
    public void shouldUpdateImportOfOntologyIri() throws OWLOntologyCreationException {
        IRI oldOntologyIri = IRI.create("http://example.org/old");
        IRI newOntologyIri = IRI.create("http://example.org/new");
        OWLOntologyID oldId = new OWLOntologyID(oldOntologyIri);
        OWLOntologyID newId = new OWLOntologyID(newOntologyIri);

        assertImportIsUpdated(oldId, newId, oldOntologyIri, newOntologyIri);
    }

    @Test
    public void shouldUpdateImportOfVersionIri() throws OWLOntologyCreationException {
        IRI ontologyIri = IRI.create("http://example.org/ontology");
        IRI oldVersionIri = IRI.create("http://example.org/versions/old");
        IRI newVersionIri = IRI.create("http://example.org/versions/new");
        OWLOntologyID oldId = new OWLOntologyID(ontologyIri, oldVersionIri);
        OWLOntologyID newId = new OWLOntologyID(ontologyIri, newVersionIri);

        assertImportIsUpdated(oldId, newId, oldVersionIri, newVersionIri);
    }

    private static void assertImportIsUpdated(OWLOntologyID oldId,
                                              OWLOntologyID newId,
                                              IRI oldImportIri,
                                              IRI newImportIri) throws OWLOntologyCreationException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = manager.createOntology(oldId);
        OWLOntology importingOntology = manager.createOntology(IMPORTING_ONTOLOGY_IRI);
        OWLDataFactory dataFactory = manager.getOWLDataFactory();
        OWLImportsDeclaration oldImport = dataFactory.getOWLImportsDeclaration(oldImportIri);
        OWLImportsDeclaration newImport = dataFactory.getOWLImportsDeclaration(newImportIri);
        manager.applyChange(new AddImport(importingOntology, oldImport));

        List<OWLOntologyChange> changes = ChangeOntologyIRI.getChanges(ontology, newId);

        assertThat(changes, contains(
                new SetOntologyID(ontology, newId),
                new RemoveImport(importingOntology, oldImport),
                new AddImport(importingOntology, newImport)));
    }
}
