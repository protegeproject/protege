package org.protege.editor.owl.model.hierarchy.roots;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.util.OWLAxiomVisitorAdapter;

public class ProtegeOrphanFinder  {
    private OWLOntologyManager manager;
    private Set<OWLOntology> ontologies;
    private OWLClass root;
    private TerminalElementFinder<OWLClass> rootFinder;
    private ParentClassExtractor parentClassExtractor;
    
    public ProtegeOrphanFinder(OWLOntologyManager manager, Set<OWLOntology> ontologies) {
        this.manager = manager;
        this.ontologies = ontologies;
        root = manager.getOWLDataFactory().getOWLThing();
        parentClassExtractor = new ParentClassExtractor();
        rootFinder = new TerminalElementFinder<OWLClass>(new Relation<OWLClass>() {
            public Collection<OWLClass> getR(OWLClass cls) {
                Collection<OWLClass> parents = getParents(cls);
                parents.remove(root);
                return parents;
            }
        });
    }
    
    public void setOntologies(Set<OWLOntology> ontologies) {
        this.ontologies = ontologies;
    }
    
    public void initializeListener() {
        manager.addOntologyChangeListener(new OWLOntologyChangeListener() {
           public void ontologiesChanged(List<? extends OWLOntologyChange> changes) throws OWLException {
               for (OWLOntologyChange change : changes) {
                   if (change.getOntology() != null && ontologies.contains(change.getOntology())) {
                       updateImplicitRoots(change);
                   }
               }
            } 
        });
    }

    public Set<OWLClass> getParents(OWLClass object) {
        // If the object is thing then there are no
        // parents
        if (object.equals(root)) {
            return Collections.emptySet();
        }
        Set<OWLClass> result = new HashSet<OWLClass>();
        // Thing if the object is a root class
        if (rootFinder.getTerminalElements().contains(object)) {
            result.add(root);
        }
        // Not a root, so must have another parent
        parentClassExtractor.reset();
        parentClassExtractor.setCurrentClass(object);
        for (OWLOntology ont : ontologies) {
            for (OWLAxiom ax : ont.getAxioms(object)) {
                ax.accept(parentClassExtractor);
            }
        }
        result.addAll(parentClassExtractor.getResult());
        return result;
    }
    
    public Set<OWLClass> getOrphanedClasses() {
        return rootFinder.getTerminalElements();
    }
    
    public void rebuildImplicitRoots() {
        rootFinder.clear();
        for (OWLOntology ont : ontologies) {
            Set<OWLClass> ref = ont.getClassesInSignature();
            rootFinder.appendTerminalElements(ref);
        }
        rootFinder.finish();
    }
    
    public void updateImplicitRoots(OWLOntologyChange change) {
        boolean remove = change instanceof RemoveAxiom;
        OWLAxiom axiom = change.getAxiom();
        Set<OWLClass> possibleTerminalElements = new HashSet<OWLClass>();
        Set<OWLClass> notInOntologies = new HashSet<OWLClass>();
        for (OWLEntity entity : axiom.getSignature()) {
            if (!(entity instanceof OWLClass) || entity.equals(root)) {
                continue;
            }
            OWLClass cls = (OWLClass) entity;
            if (remove && !containsReference(cls)) {
                notInOntologies.add(cls);
                continue;
            }
            possibleTerminalElements.add(cls);
        }
        possibleTerminalElements.addAll(rootFinder.getTerminalElements());
        possibleTerminalElements.removeAll(notInOntologies);
        rootFinder.findTerminalElements(possibleTerminalElements);
    }
    
    public boolean containsReference(OWLClass object) {
        for (OWLOntology ont : ontologies) {
            if (ont.containsClassInSignature(object.getIRI())) {
                return true;
            }
        }
        return false;
    }
    
    private class ParentClassExtractor extends OWLAxiomVisitorAdapter {

        private NamedClassExtractor extractor = new NamedClassExtractor();

        private OWLClass current;


        public void setCurrentClass(OWLClass current) {
            this.current = current;
        }


        public void reset() {
            extractor.reset();
        }


        public Set<OWLClass> getResult() {
            return extractor.getResult();
        }


        public void visit(OWLSubClassOfAxiom axiom) {
            axiom.getSuperClass().accept(extractor);
        }


        public void visit(OWLEquivalentClassesAxiom axiom) {
            for (OWLClassExpression desc : axiom.getClassExpressions()) {
                if (desc.equals(current)) {
                    continue;
                }
                desc.accept(extractor);
            }
        }
    }
}
