package org.protege.editor.owl.model.hierarchy;

import java.io.File;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.coode.owl.functionalrenderer.OWLFunctionalSyntaxRenderer;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.io.OWLRendererException;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;

public class AssertedClassHierarchyTest extends TestCase {
    private Logger log = Logger.getLogger(AssertedClassHierarchyTest.class);
    
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
    
    public void testTwoParents() throws OWLOntologyCreationException, URISyntaxException {
        String namespace = "http://tigraworld.com/protege/twoParents.owl#";
        AssertedClassHierarchyProvider2 hierarchy = installOntology("junit/ontologies/tree/twoParents.owl");
        
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
    
    public void testAddGCA() throws OWLOntologyCreationException, URISyntaxException, OWLOntologyChangeException, OWLRendererException {
        String namespace = "http://tigraworld.com/protege/twoParents.owl#";
        AssertedClassHierarchyProvider2 hierarchy = installOntology("junit/ontologies/tree/twoParents.owl");
        
        OWLOntology ontology = manager.getOntologies().iterator().next();
        
        assertEquals(3, hierarchy.getChildren(factory.getOWLThing()).size());
        
        OWLClass x = factory.getOWLClass(new URI(namespace + "X"));
        OWLObjectProperty p = factory.getOWLObjectProperty(new URI(namespace + "p"));
        OWLClass y = factory.getOWLClass(new URI(namespace + "Y"));
        OWLClass z = factory.getOWLClass(new URI(namespace + "Z"));
        OWLAxiom gca = factory.getOWLSubClassAxiom(
                             factory.getOWLObjectIntersectionOf(x, factory.getOWLObjectSomeRestriction(p, y)),
                             z);
        OWLOntologyChange change = new AddAxiom(ontology, gca);
        manager.applyChange(change);
        
        if (log.isDebugEnabled()) {
            StringWriter writer = new StringWriter();
            OWLFunctionalSyntaxRenderer renderer = new OWLFunctionalSyntaxRenderer(manager);
            renderer.render(ontology, writer);
            log.debug(writer.toString());
        }
        
        assertEquals(6, hierarchy.getChildren(factory.getOWLThing()).size());
    }

}
