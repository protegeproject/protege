package org.protege.editor.owl.model.library;

import java.io.File;
import java.net.URI;

import junit.framework.TestCase;

import org.protege.editor.owl.model.library.folder.XmlBaseAlgorithm;

public class XmlBaseTest extends TestCase {
    public static final String PIZZA_NAME = "http://www.co-ode.org/ontologies/pizza/2005/10/18/pizza.owl";

    public void testOwlXml() {
        File pizza = new File("junit/ontologies/update/pizza-xml.owl");
        XmlBaseAlgorithm algorithm = new XmlBaseAlgorithm();
        assertTrue(algorithm.getSuggestions(pizza).size() == 1);
        assertTrue(algorithm.getSuggestions(pizza).contains(URI.create(PIZZA_NAME)));
    }
    
    public void testRdfXml() {
        File pizza = new File("junit/ontologies/update/pizza.owl");
        XmlBaseAlgorithm algorithm = new XmlBaseAlgorithm();
        assertTrue(algorithm.getSuggestions(pizza).size() == 1);
        assertTrue(algorithm.getSuggestions(pizza).contains(URI.create(PIZZA_NAME)));
    }
}
