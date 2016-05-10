package org.protege.editor.owl.model.history;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HistoryTest {
    public static final String NS = "http://protege.org/ontologies/History.owl";
    public static final String ALT_NS = "http://protege.org/ontologies/HistoryII.owl";
    public static final OWLClass A = OWLManager.getOWLDataFactory().getOWLClass(IRI.create(NS + "#A"));
    public static final OWLClass B = OWLManager.getOWLDataFactory().getOWLClass(IRI.create(NS + "#B"));
    public static final OWLClass C = OWLManager.getOWLDataFactory().getOWLClass(IRI.create(NS + "#C"));
    public static final OWLClass D = OWLManager.getOWLDataFactory().getOWLClass(IRI.create(NS + "#D"));
    public static final OWLClass E = OWLManager.getOWLDataFactory().getOWLClass(IRI.create(NS + "#E"));
    public static final OWLClass F = OWLManager.getOWLDataFactory().getOWLClass(IRI.create(NS + "#F"));
    public static final OWLClass G = OWLManager.getOWLDataFactory().getOWLClass(IRI.create(NS + "#G"));

    public static final OWLAxiom AXIOM1 = OWLManager.getOWLDataFactory().getOWLSubClassOfAxiom(B, A);
    public static final OWLAxiom AXIOM2 = OWLManager.getOWLDataFactory().getOWLSubClassOfAxiom(C, A);
    
    private OWLOntologyManager manager;
    private OWLDataFactory factory;
    private OWLOntology ontology1, ontology2;
    private HistoryManager historyManager;
    
    @Before
    public void setUp() throws OWLOntologyCreationException {
        manager = OWLManager.createOWLOntologyManager();
        factory = manager.getOWLDataFactory();
        ontology1 = manager.createOntology(IRI.create(NS));
        ontology2 = manager.createOntology(IRI.create(ALT_NS));
        List<OWLOntologyChange> changes = new ArrayList<>();
        changes.add(new AddAxiom(ontology1, AXIOM1));
        changes.add(new AddAxiom(ontology2, AXIOM2));
        manager.applyChanges(changes);
        historyManager = new HistoryManagerImpl(manager);
        manager.addOntologyChangeListener(changes1 -> {
            historyManager.logChanges(changes1);
        });
    }

    @Test
    public void testIntialState() {
        assertTrue(!historyManager.canRedo());
        assertTrue(!historyManager.canUndo());
    }

    @Test
    public void testTwoRedos() {
        OWLAxiom axiom3 = factory.getOWLSubClassOfAxiom(D, A);
        manager.addAxiom(ontology1, axiom3);
        OWLAxiom axiom4 = factory.getOWLSubClassOfAxiom(E, D);
        manager.addAxiom(ontology1, axiom4);
        OWLAxiom axiom5 = factory.getOWLSubClassOfAxiom(F, E);
        manager.addAxiom(ontology2, axiom5);
        historyManager.undo();
        historyManager.undo();
        
        assertFalse(ontology2.containsAxiom(axiom5));
        assertFalse(ontology1.containsAxiom(axiom4));
        assertTrue(ontology1.containsAxiom(axiom3));
        
        historyManager.redo();
        historyManager.redo();
        assertTrue(ontology2.containsAxiom(axiom5));
        assertTrue(ontology1.containsAxiom(axiom4));
        assertTrue(ontology1.containsAxiom(axiom3));
        assertFalse(historyManager.canRedo());
        assertTrue(historyManager.canUndo());
    }

    @Test
    public void testReverseChanges01() {
        OWLAxiom axiom3 = factory.getOWLSubClassOfAxiom(D, A);
        OWLAxiom axiom4 = factory.getOWLSubClassOfAxiom(E, D);
        
        assertFalse(ontology1.containsAxiom(axiom3));
        assertFalse(ontology1.containsAxiom(axiom4));
        assertFalse(ontology2.containsAxiom(axiom3));
        assertFalse(ontology2.containsAxiom(axiom4));

        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        changes.add(new AddAxiom(ontology1, axiom3));
        changes.add(new RemoveAxiom(ontology1, axiom3));
        changes.add(new AddAxiom(ontology1, axiom4));
        manager.applyChanges(changes);
        
        assertFalse(ontology1.containsAxiom(axiom3));
        assertTrue(ontology1.containsAxiom(axiom4));
        
        historyManager.undo();
        
        assertFalse(ontology1.containsAxiom(axiom3));
        assertFalse(ontology1.containsAxiom(axiom4));        
        
        historyManager.redo();
        
        assertFalse(ontology1.containsAxiom(axiom3));
        assertTrue(ontology1.containsAxiom(axiom4));          
        
    }

    @Test
    public void testReverseChanges02() {
        OWLAxiom axiom3 = factory.getOWLSubClassOfAxiom(D, A);
        OWLAxiom axiom4 = factory.getOWLSubClassOfAxiom(E, D);

        assertFalse(ontology1.containsAxiom(axiom3));
        assertFalse(ontology1.containsAxiom(axiom4));
        assertFalse(ontology2.containsAxiom(axiom3));
        assertFalse(ontology2.containsAxiom(axiom4));

        manager.addAxiom(ontology1, axiom3);

        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        changes.add(new AddAxiom(ontology1, axiom3));
        changes.add(new RemoveAxiom(ontology1, axiom3));
        changes.add(new AddAxiom(ontology1, axiom4));
        manager.applyChanges(changes);

        assertFalse(ontology1.containsAxiom(axiom3));
        assertTrue(ontology1.containsAxiom(axiom4));

        historyManager.undo();

        assertFalse(ontology1.containsAxiom(axiom4));

        historyManager.redo();

        assertFalse(ontology1.containsAxiom(axiom3));
        assertTrue(ontology1.containsAxiom(axiom4));

    }

    @Test
    public void testReverseChanges03() {
        OWLAxiom axiom3 = factory.getOWLSubClassOfAxiom(D, A);
        OWLAxiom axiom4 = factory.getOWLSubClassOfAxiom(E, D);
        
        assertFalse(ontology1.containsAxiom(axiom3));
        assertFalse(ontology1.containsAxiom(axiom4));
        assertFalse(ontology2.containsAxiom(axiom3));
        assertFalse(ontology2.containsAxiom(axiom4));

        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        changes.add(new RemoveAxiom(ontology1, axiom3));
        changes.add(new AddAxiom(ontology1, axiom3));
        changes.add(new AddAxiom(ontology1, axiom4));
        manager.applyChanges(changes);
        
        assertTrue(ontology1.containsAxiom(axiom3));
        assertTrue(ontology1.containsAxiom(axiom4));
        
        historyManager.undo();
        
        assertFalse(ontology1.containsAxiom(axiom4));
        
        historyManager.redo();
        
        assertTrue(ontology1.containsAxiom(axiom3));
        assertTrue(ontology1.containsAxiom(axiom4));          
        
    }

    @Test
    public void testReverseChanges04() {
        OWLAxiom axiom3 = factory.getOWLSubClassOfAxiom(D, A);
        OWLAxiom axiom4 = factory.getOWLSubClassOfAxiom(E, D);
        
        assertFalse(ontology1.containsAxiom(axiom3));
        assertFalse(ontology1.containsAxiom(axiom4));
        assertFalse(ontology2.containsAxiom(axiom3));
        assertFalse(ontology2.containsAxiom(axiom4));
        
        manager.addAxiom(ontology1, axiom3);

        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        changes.add(new RemoveAxiom(ontology1, axiom3));
        changes.add(new AddAxiom(ontology1, axiom3));
        changes.add(new AddAxiom(ontology1, axiom4));
        manager.applyChanges(changes);
        
        assertTrue(ontology1.containsAxiom(axiom3));
        assertTrue(ontology1.containsAxiom(axiom4));
        
        historyManager.undo();
        
        assertTrue(ontology1.containsAxiom(axiom3));
        assertFalse(ontology1.containsAxiom(axiom4));        
        
        historyManager.redo();
        
        assertTrue(ontology1.containsAxiom(axiom3));
        assertTrue(ontology1.containsAxiom(axiom4));          
        
    }

}
