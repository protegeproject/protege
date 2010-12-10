package org.protege.editor.owl.model.library;

import java.io.File;
import java.net.URI;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.library.folder.RdfXmlNameAlgorithm;

public class RdfXmlNameTest extends TestCase {
    private static final Logger logger = Logger.getLogger(RdfXmlNameTest.class);

    public void testSimple() {
        File pizza = new File("junit/ontologies/update/pizza.owl");
        RdfXmlNameAlgorithm algorithm = new RdfXmlNameAlgorithm();
        assertTrue(algorithm.getSuggestions(pizza).size() == 1);
        assertTrue(algorithm.getSuggestions(pizza).contains(URI.create(XmlBaseTest.PIZZA_NAME)));
    }
    
    public void testAmbiguous() {
    	File ambiguous = new File("junit/ontologies/update/Ambiguous.owl");
    	RdfXmlNameAlgorithm algorithm = new RdfXmlNameAlgorithm();
    	assertTrue(algorithm.getSuggestions(ambiguous).size()==1);
    	assertTrue(algorithm.getSuggestions(ambiguous).contains(URI.create("http://www.test.com/right.owl")));
    }
    
    public void testVersioned() {
        String name = "http://www.tigraworld.com/protege/determinant.owl";
        String version = "http://www.tigraworld.com/protege/determinant-2007-08-01.owl";
        File versioned = new File("junit/ontologies/update/versioned.owl");
        RdfXmlNameAlgorithm algorithm = new RdfXmlNameAlgorithm();
        assertTrue(algorithm.getSuggestions(versioned).size() == 1);
        assertTrue(algorithm.getSuggestions(versioned).contains(URI.create(version)));
        
        algorithm.setAssumeLatest(true);
        assertTrue(algorithm.getSuggestions(versioned).size() == 2);
        assertTrue(algorithm.getSuggestions(versioned).contains(URI.create(name)));
        assertTrue(algorithm.getSuggestions(versioned).contains(URI.create(version)));
    }
}
