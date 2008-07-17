package org.protege.editor.owl.model.hierarchy;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import junit.framework.TestCase;

import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;

public class AssertedClassHierarchyTest extends TestCase {
    private OWLOntologyManager manager;
    private OWLDataFactory factory;
    
    protected AssertedClassHierarchyProvider2 installOntology(String path) throws OWLOntologyCreationException {
        manager = OWLManager.createOWLOntologyManager();
        factory = manager.getOWLDataFactory();
        manager.loadOntologyFromPhysicalURI(new File(path).toURI());
        AssertedClassHierarchyProvider2 hierarchy = new AssertedClassHierarchyProvider2(manager);
        hierarchy.setOntologies(manager.getOntologies());
        return hierarchy;
    }
    
    public void testSimpleLoop() throws OWLOntologyCreationException, URISyntaxException {
        String namespace = "http://tigraworld.com/protege/simpleLoop.owl#";
        AssertedClassHierarchyProvider2 hierarchy = installOntology("junit/ontologies/tree/simpleLoop.owl");
        
        OWLClass a = factory.getOWLClass(new URI(namespace + "A"));
        OWLClass b = factory.getOWLClass(new URI(namespace + "B"));
        OWLClass c = factory.getOWLClass(new URI(namespace + "C"));
        
        Set<OWLClass> children = hierarchy.getChildren(factory.getOWLThing());
        assertEquals(3, children.size());
        assertTrue(children.contains(a));
        assertTrue(children.contains(b));
        assertTrue(children.contains(c));
        
        assertTrue(hierarchy.getChildren(a).isEmpty());
        assertTrue(hierarchy.getChildren(b).isEmpty());
        assertTrue(hierarchy.getChildren(c).isEmpty());
    }

}
