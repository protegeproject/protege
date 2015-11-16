package org.protege.editor.owl.model.repository;

import com.google.common.base.Optional;
import junit.framework.TestCase;
import org.protege.editor.owl.model.repository.extractors.RdfXmlExtractor;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyID;

import java.io.File;

public class RDFExtractorTest extends TestCase {
    public static final String PIZZA_LOCATION  = "src/test/resources/ontologies/pizza.owl";
    public static final String PIZZA_LOCATION2 = "src/test/resources/ontologies/pizza-functional.owl";
    public static final String PIZZA_NAME      = "http://www.co-ode.org/ontologies/pizza/2005/10/18/pizza.owl";
    
    public static final String AMBIGUOUS_LOCATION = "src/test/resources/ontologies/AmbiguousName.owl";
    public static final String AMBIGUOUS_NAME     = "http://www.test.com/right.owl";
    
    public static final String VERSIONED_LOCATION  = "src/test/resources/ontologies/VersionedOntology.owl";
    public static final String VERSIONED_LOCATION2 = "src/test/resources/ontologies/VersionedOntology-functional.owl";
    public static final String VERSIONED_NAME      = "http://www.tigraworld.com/protege/VersionedOntology.owl";
    public static final String VERSIONED_VERSION   = "http://www.tigraworld.com/protege/Version1.owl";
    
    public void testPizza01() {
        RdfXmlExtractor extractor = new RdfXmlExtractor();
        Optional<OWLOntologyID> id = extractor.getOntologyId(new File(PIZZA_LOCATION).toURI());
        assertTrue(id.get().getOntologyIRI().get().equals(IRI.create(PIZZA_NAME)));
        assertTrue(!id.get().getVersionIRI().isPresent());
    }
    
    public void testPizza02() {
        MasterOntologyIDExtractor extractor = new MasterOntologyIDExtractor();
        Optional<OWLOntologyID> id = extractor.getOntologyId(new File(PIZZA_LOCATION).toURI());
        assertTrue(id.get().getOntologyIRI().get().equals(IRI.create(PIZZA_NAME)));
        assertTrue(!id.get().getVersionIRI().isPresent());
    }
    
    public void testPizza03() {
        RdfXmlExtractor extractor = new RdfXmlExtractor();
        assertTrue(!extractor.getOntologyId(new File(PIZZA_LOCATION2).toURI()).isPresent());
    }
    
    public void testPizza04() {
        MasterOntologyIDExtractor extractor = new MasterOntologyIDExtractor();
        Optional<OWLOntologyID> id = extractor.getOntologyId(new File(PIZZA_LOCATION2).toURI());
        assertTrue(id.get().getOntologyIRI().get().equals(IRI.create(PIZZA_NAME)));
        assertTrue(!id.get().getVersionIRI().isPresent());
    }
    
    public void testAmbiguous01() {
        RdfXmlExtractor extractor = new RdfXmlExtractor();
        Optional<OWLOntologyID> id = extractor.getOntologyId(new File(AMBIGUOUS_LOCATION).toURI());
        assertTrue(id.get().getOntologyIRI().get().equals(IRI.create(AMBIGUOUS_NAME)));
        assertTrue(!id.get().getVersionIRI().isPresent());
    }
    
    public void testAmbiguous02() {
        MasterOntologyIDExtractor extractor = new MasterOntologyIDExtractor();
        Optional<OWLOntologyID> id = extractor.getOntologyId(new File(AMBIGUOUS_LOCATION).toURI());
        assertTrue(id.get().getOntologyIRI().get().equals(IRI.create(AMBIGUOUS_NAME)));
        assertTrue(!id.get().getVersionIRI().isPresent());
    }
    
    public void testVersioned01() {
        RdfXmlExtractor extractor = new RdfXmlExtractor();
        Optional<OWLOntologyID> id = extractor.getOntologyId(new File(VERSIONED_LOCATION).toURI());
        assertTrue(id.get().getOntologyIRI().get().equals(IRI.create(VERSIONED_NAME)));
        assertTrue(id.get().getVersionIRI().get().equals(IRI.create(VERSIONED_VERSION)));
    }
    
    public void testVersioned02() {
        MasterOntologyIDExtractor extractor = new MasterOntologyIDExtractor();
        Optional<OWLOntologyID> id = extractor.getOntologyId(new File(VERSIONED_LOCATION).toURI());
        assertTrue(id.get().getOntologyIRI().get().equals(IRI.create(VERSIONED_NAME)));
        assertTrue(id.get().getVersionIRI().get().equals(IRI.create(VERSIONED_VERSION)));
    }
    
    public void testVersioned03() {
        RdfXmlExtractor extractor = new RdfXmlExtractor();
        assertTrue(!extractor.getOntologyId(new File(VERSIONED_LOCATION2).toURI()).isPresent());
    }
    
    public void testVersioned04() {
        MasterOntologyIDExtractor extractor = new MasterOntologyIDExtractor();
        Optional<OWLOntologyID> id = extractor.getOntologyId(new File(VERSIONED_LOCATION2).toURI());
        assertTrue(id.get().getOntologyIRI().get().equals(IRI.create(VERSIONED_NAME)));
        assertTrue(id.get().getVersionIRI().get().equals(IRI.create(VERSIONED_VERSION)));
    }
}
