package org.protege.editor.owl.model.hierarchy;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.protege.editor.owl.util.JunitUtil;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.io.OWLRendererException;
import org.semanticweb.owl.model.*;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

public class AssertedClassHierarchyTest extends TestCase {
    private Logger log = Logger.getLogger(AssertedClassHierarchyTest.class);
    
    
    public static String NEW_ONTOLOGY_URI = "http://www.tigraworld.com/protege/1";
    
    private OWLOntologyManager manager;
    private OWLDataFactory factory;
    AssertedClassHierarchyProvider hierarchy;
    
    protected void init() {
        manager = OWLManager.createOWLOntologyManager();
        factory = manager.getOWLDataFactory();
        hierarchy = new AssertedClassHierarchyProvider(manager);
    }
    
    protected OWLOntology createOntology() throws OWLOntologyCreationException, URISyntaxException {
        init();
        OWLOntology ontology = manager.createOntology(IRI.create(NEW_ONTOLOGY_URI));
        hierarchy.setOntologies(manager.getOntologies());
        return ontology;
    }
    
    protected OWLOntology installOntology(String path) throws OWLOntologyCreationException {
        init();
        OWLOntology ontology = manager.loadOntologyFromPhysicalURI(new File(path).toURI());
        hierarchy.setOntologies(manager.getOntologies());
        return ontology;
    }
    
    public void testSimpleLoop() throws OWLOntologyCreationException, URISyntaxException, OWLOntologyChangeException {
        String namespace = "http://tigraworld.com/protege/simpleLoop.owl#";
        installOntology("junit/ontologies/tree/simpleLoop.owl");
        OWLOntology ontology = manager.getOntologies().iterator().next();
        
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
        
        OWLAxiom axiom = factory.getOWLSubClassOfAxiom(a, c);
        manager.applyChange(new RemoveAxiom(ontology, axiom));
        
        assertEquals(1, hierarchy.getChildren(factory.getOWLThing()).size());
        assertTrue(hierarchy.getChildren(factory.getOWLThing()).contains(a));
        
        assertEquals(1, hierarchy.getChildren(a).size());
        assertTrue(hierarchy.getChildren(a).contains(b));
        
        assertEquals(1, hierarchy.getChildren(b).size());
        assertTrue(hierarchy.getChildren(b).contains(c));

        assertEquals(0, hierarchy.getChildren(c).size());
    }
    
    public void testTwoParents() throws OWLOntologyCreationException, URISyntaxException {
        String namespace = "http://tigraworld.com/protege/twoParents.owl#";
        installOntology("junit/ontologies/tree/twoParents.owl");
        
        OWLClass a = factory.getOWLClass(new URI(namespace + "A"));
        OWLClass b = factory.getOWLClass(new URI(namespace + "B"));
        OWLClass c = factory.getOWLClass(new URI(namespace + "C"));
        OWLClass d = factory.getOWLClass(new URI(namespace + "D"));
        OWLClass e = factory.getOWLClass(new URI(namespace + "E"));
        
        assertEquals(3, hierarchy.getChildren(factory.getOWLThing()).size());
        assertTrue(hierarchy.getChildren(factory.getOWLThing()).contains(a));
        assertTrue(hierarchy.getChildren(factory.getOWLThing()).contains(b));
        assertTrue(hierarchy.getChildren(factory.getOWLThing()).contains(d));
        
        assertEquals(1, hierarchy.getChildren(a).size());
        assertTrue(hierarchy.getChildren(a).contains(c));
        
        assertEquals(1, hierarchy.getChildren(b).size());
        assertTrue(hierarchy.getChildren(b).contains(e));
    }
    
    public void testAddGCA() 
    throws OWLOntologyCreationException, URISyntaxException, OWLOntologyChangeException, OWLRendererException {
        String namespace = "http://tigraworld.com/protege/twoParents.owl#";
        installOntology("junit/ontologies/tree/twoParents.owl");
        
        OWLOntology ontology = manager.getOntologies().iterator().next();
        
        assertEquals(3, hierarchy.getChildren(factory.getOWLThing()).size());
        
        OWLClass x = factory.getOWLClass(new URI(namespace + "X"));
        OWLObjectProperty p = factory.getOWLObjectProperty(new URI(namespace + "p"));
        OWLClass y = factory.getOWLClass(new URI(namespace + "Y"));
        OWLClass z = factory.getOWLClass(new URI(namespace + "Z"));
        OWLAxiom gca = factory.getOWLSubClassOfAxiom(
                             factory.getOWLObjectIntersectionOf(x, factory.getOWLObjectSomeValuesFrom(p, y)),
                             z);
        OWLOntologyChange change = new AddAxiom(ontology, gca);
        manager.applyChange(change);
        
        JunitUtil.printOntology(manager, ontology);
        
        assertEquals(6, hierarchy.getChildren(factory.getOWLThing()).size());
        
        change = new RemoveAxiom(ontology, gca);
        manager.applyChange(change);
        
        assertEquals(3, hierarchy.getChildren(factory.getOWLThing()).size());
    }

    public void testRemoveNonOrphaned() throws OWLOntologyCreationException, URISyntaxException, OWLOntologyChangeException {
        OWLOntology ontology = createOntology();
        OWLClass a = factory.getOWLClass(new URI(NEW_ONTOLOGY_URI + "#A"));
        OWLClass b = factory.getOWLClass(new URI(NEW_ONTOLOGY_URI + "#B"));
        CollectingHierarchyListener listener = new CollectingHierarchyListener();
        hierarchy.addListener(listener);
        
        manager.addAxiom(ontology, factory.getOWLSubClassOfAxiom(a, factory.getOWLThing()));
        assertTrue(listener.getCollectedNodes().contains(factory.getOWLThing()));
        listener.clear();
        
        OWLAxiom temporary = factory.getOWLSubClassOfAxiom(b, factory.getOWLThing());
        manager.addAxiom(ontology, temporary);
        assertTrue(listener.getCollectedNodes().contains(factory.getOWLThing()));
        listener.clear();
        
        manager.addAxiom(ontology, factory.getOWLSubClassOfAxiom(b, a));
        listener.clear();

        JunitUtil.printOntology(manager, ontology);
        
        manager.applyChange(new RemoveAxiom(ontology, temporary));
        assertTrue(listener.getCollectedNodes().contains(factory.getOWLThing()));
        
    }
}
