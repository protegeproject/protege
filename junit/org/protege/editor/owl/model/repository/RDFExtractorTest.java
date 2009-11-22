package org.protege.editor.owl.model.repository;

import java.io.File;

import junit.framework.TestCase;

import org.protege.editor.owl.model.repository.extractors.RdfXmlExtractor;
import org.semanticweb.owlapi.model.OWLOntologyID;

public class RDFExtractorTest extends TestCase {
    public static final String PIZZA_LOCATION  = "junit/ontologies/pizza.owl";
    public static final String PIZZA_LOCATION2 = "junit/ontologies/pizza-functional.owl";
    public static final String PIZZA_NAME      = "http://www.co-ode.org/ontologies/pizza/2005/10/18/pizza.owl";
    
    public static final String AMBIGUOUS_LOCATION = "junit/ontologies/AmbiguousName.owl";
    public static final String AMBIGUOUS_NAME     = "http://www.test.com/right.owl";
    
    public static final String VERSIONED_LOCATION  = "junit/ontologies/VersionedOntology.owl";
    public static final String VERSIONED_LOCATION2 = "junit/ontologies/VersionedOntology-functional.owl";
    public static final String VERSIONED_NAME      = "http://www.tigraworld.com/protege/VersionedOntology.owl";
    public static final String VERSIONED_VERSION   = "http://www.tigraworld.com/protege/Version1.owl";
    
    public void testPizza01() {
        RdfXmlExtractor extractor = new RdfXmlExtractor();
        extractor.setPhysicalAddress(new File(PIZZA_LOCATION).toURI());
        OWLOntologyID id = extractor.getOntologyId();
        assertTrue(id.getOntologyIRI().toString().equals(PIZZA_NAME));
        assertTrue(id.getVersionIRI() == null);
    }
    
    public void testPizza02() {
        MasterOntologyIDExtractor extractor = new MasterOntologyIDExtractor();
        extractor.setPhysicalAddress(new File(PIZZA_LOCATION).toURI());
        OWLOntologyID id = extractor.getOntologyId();
        assertTrue(id.getOntologyIRI().toString().equals(PIZZA_NAME));
        assertTrue(id.getVersionIRI() == null);
    }
    
    public void testPizza03() {
        RdfXmlExtractor extractor = new RdfXmlExtractor();
        extractor.setPhysicalAddress(new File(PIZZA_LOCATION2).toURI());
        assertTrue(extractor.getOntologyId() == null);
    }
    
    public void testPizza04() {
        MasterOntologyIDExtractor extractor = new MasterOntologyIDExtractor();
        extractor.setPhysicalAddress(new File(PIZZA_LOCATION2).toURI());
        OWLOntologyID id = extractor.getOntologyId();
        assertTrue(id.getOntologyIRI().toString().equals(PIZZA_NAME));
        assertTrue(id.getVersionIRI() == null);
    }
    
    public void testAmbiguous01() {
        RdfXmlExtractor extractor = new RdfXmlExtractor();
        extractor.setPhysicalAddress(new File(AMBIGUOUS_LOCATION).toURI());
        OWLOntologyID id = extractor.getOntologyId();
        assertTrue(id.getOntologyIRI().toString().equals(AMBIGUOUS_NAME));
        assertTrue(id.getVersionIRI() == null);
    }
    
    public void testAmbiguous02() {
        MasterOntologyIDExtractor extractor = new MasterOntologyIDExtractor();
        extractor.setPhysicalAddress(new File(AMBIGUOUS_LOCATION).toURI());
        OWLOntologyID id = extractor.getOntologyId();
        assertTrue(id.getOntologyIRI().toString().equals(AMBIGUOUS_NAME));
        assertTrue(id.getVersionIRI() == null);
    }
    
    public void testVersioned01() {
        RdfXmlExtractor extractor = new RdfXmlExtractor();
        extractor.setPhysicalAddress(new File(VERSIONED_LOCATION).toURI());
        OWLOntologyID id = extractor.getOntologyId();
        assertTrue(id.getOntologyIRI().toString().equals(VERSIONED_NAME));
        assertTrue(id.getVersionIRI().toString().equals(VERSIONED_VERSION));
    }
    
    public void testVersioned02() {
        MasterOntologyIDExtractor extractor = new MasterOntologyIDExtractor();
        extractor.setPhysicalAddress(new File(VERSIONED_LOCATION).toURI());
        OWLOntologyID id = extractor.getOntologyId();
        assertTrue(id.getOntologyIRI().toString().equals(VERSIONED_NAME));
        assertTrue(id.getVersionIRI().toString().equals(VERSIONED_VERSION));
    }
    
    public void testVersioned03() {
        RdfXmlExtractor extractor = new RdfXmlExtractor();
        extractor.setPhysicalAddress(new File(VERSIONED_LOCATION2).toURI());
        assertTrue(extractor.getOntologyId() == null);
    }
    
    public void testVersioned04() {
        MasterOntologyIDExtractor extractor = new MasterOntologyIDExtractor();
        extractor.setPhysicalAddress(new File(VERSIONED_LOCATION2).toURI());
        OWLOntologyID id = extractor.getOntologyId();
        assertTrue(id.getOntologyIRI().toString().equals(VERSIONED_NAME));
        assertTrue(id.getVersionIRI().toString().equals(VERSIONED_VERSION));
    }
}
