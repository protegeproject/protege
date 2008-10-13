package org.protege.editor.owl.model.hierarchy;

import org.protege.editor.owl.model.hierarchy.roots.Relation;
import org.protege.editor.owl.model.hierarchy.roots.TerminalElementFinder;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLAxiomVisitorAdapter;
import org.semanticweb.owl.util.OWLDescriptionVisitorAdapter;

import java.util.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 17-Jan-2007<br><br>
 */
public class AssertedClassHierarchyProvider2 extends AbstractOWLObjectHierarchyProvider<OWLClass> {

    private OWLOntologyManager owlOntologyManager;

    private Set<OWLOntology> ontologies;

    private OWLClass root;

    private ParentClassExtractor parentClassExtractor;

    private ChildClassExtractor childClassExtractor;

    private OWLOntologyChangeListener listener;

    private TerminalElementFinder<OWLClass> rootFinder;

    private Set<OWLClass> nodesToUpdate = new HashSet<OWLClass>();


    public AssertedClassHierarchyProvider2(OWLOntologyManager owlOntologyManager) {
        super(owlOntologyManager);
        this.owlOntologyManager = owlOntologyManager;
        ontologies = new HashSet<OWLOntology>();
        rootFinder = new TerminalElementFinder<OWLClass>(new Relation<OWLClass>() {
            public Collection<OWLClass> getR(OWLClass cls) {
                Collection<OWLClass> parents = getParents(cls);
                parents.remove(root);
                return parents;
            }
        });

        parentClassExtractor = new ParentClassExtractor();
        childClassExtractor = new ChildClassExtractor();
        listener = new OWLOntologyChangeListener() {
            public void ontologiesChanged(List<? extends OWLOntologyChange> changes) {
                handleChanges(changes);
            }
        };
        getManager().addOntologyChangeListener(listener);
    }


    /**
     * Sets the ontologies that this hierarchy provider should use
     * in order to determine the hierarchy.
     */
    public void setOntologies(Set<OWLOntology> ontologies) {
        this.ontologies = new HashSet<OWLOntology>(ontologies);
        nodesToUpdate.clear();
        if (root == null) {
            root = owlOntologyManager.getOWLDataFactory().getOWLThing();
        }
        rebuildImplicitRoots();
        fireHierarchyChanged();
    }


    private void rebuildImplicitRoots() {
        rootFinder.clear();
        for (OWLOntology ont : ontologies) {
            Set<OWLClass> ref = ont.getReferencedClasses();
            rootFinder.appendTerminalElements(ref);
        }
        rootFinder.finish();
    }

    public void dispose() {
        getManager().removeOntologyChangeListener(listener);
    }


    private void handleChanges(List<? extends OWLOntologyChange> changes) {
        Set<OWLClass> oldTerminalElements = new HashSet<OWLClass>(rootFinder.getTerminalElements());
        Set<OWLClass> changedClasses = new HashSet<OWLClass>();
        changedClasses.add(root);
        for (OWLOntologyChange change : changes) {
            // only listen for changes on the appropriate ontologies
            if (ontologies.contains(change.getOntology())){
                if (change.isAxiomChange()) {
                    updateImplicitRoots(change);
                    for (OWLEntity entity : ((OWLAxiomChange) change).getEntities()) {
                        if (entity instanceof OWLClass && !entity.equals(root)) {
                            changedClasses.add((OWLClass) entity);
                        }
                    }
                }
            }
        }
        for (OWLClass cls : changedClasses) {
            registerNodeChanged(cls);
        }
        for (OWLClass cls : rootFinder.getTerminalElements()) {
            if (!oldTerminalElements.contains(cls)) {
                registerNodeChanged(cls);
            }
        }
        for (OWLClass cls : oldTerminalElements) {
            if (!rootFinder.getTerminalElements().contains(cls)) {
                registerNodeChanged(cls);
            }
        }
        notifyNodeChanges();
    }


    private void registerNodeChanged(OWLClass node) {
        nodesToUpdate.add(node);
    }


    private void notifyNodeChanges() {
        for (OWLClass node : nodesToUpdate){
            fireNodeChanged(node);
        }
        nodesToUpdate.clear();
    }


    private void updateImplicitRoots(OWLOntologyChange change) {
    	boolean remove = change instanceof RemoveAxiom;
    	OWLAxiom axiom = change.getAxiom();
    	Set<OWLClass> possibleTerminalElements = new HashSet<OWLClass>();
    	Set<OWLClass> notInOntologies = new HashSet<OWLClass>();
    	for (OWLEntity entity : axiom.getReferencedEntities()) {
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

    public Set<OWLClass> getRoots() {
        if (root == null) {
            root = owlOntologyManager.getOWLDataFactory().getOWLThing();
        }
        return Collections.singleton(root);
    }


    public Set<OWLClass> getChildren(OWLClass object) {
        Set<OWLClass> result;
        if (object.equals(root)) {
            result = new HashSet<OWLClass>();
            result.addAll(rootFinder.getTerminalElements());
            result.addAll(extractChildren(object));
            result.remove(object);
        }
        else {
            result = extractChildren(object);
            for (Iterator<OWLClass> it = result.iterator(); it.hasNext();) {
                OWLClass curChild = it.next();
                if (getAncestors(object).contains(curChild)) {
                    it.remove();
                }
            }
        }

        return result;
    }


    private Set<OWLClass> extractChildren(OWLClass parent) {
        childClassExtractor.setCurrentParentClass(parent);
        Set<OWLClass> result = new HashSet<OWLClass>(childClassExtractor.getResult());
        for (OWLOntology ont : ontologies) {
            for (OWLAxiom ax : ont.getReferencingAxioms(parent)) {
                childClassExtractor.reset();
                if (ax.isLogicalAxiom()) {
                    ax.accept(childClassExtractor);
                    result.addAll(childClassExtractor.getResult());
                }
            }
        }
        return result;
    }


    public boolean containsReference(OWLClass object) {
        for (OWLOntology ont : ontologies) {
            if (ont.containsClassReference(object.getURI())) {
                return true;
            }
        }
        return false;
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


    public Set<OWLClass> getEquivalents(OWLClass object) {
        Set<OWLClass> result = new HashSet<OWLClass>();
        for (OWLOntology ont : ontologies) {
            for (OWLDescription equiv : object.getEquivalentClasses(ont)) {
                if (!equiv.isAnonymous()) {
                    result.add((OWLClass) equiv);
                }
            }
        }
        Set<OWLClass> ancestors = getAncestors(object);
        if (ancestors.contains(object)) {
            for (OWLClass cls : ancestors) {
                if (getAncestors(cls).contains(object)) {
                    result.add(cls);
                }
            }
            result.remove(object);
            result.remove(root);
        }
        return result;
    }

// not used
//    private Set<OWLClass> extractParents(OWLClass cls) {
//        parentClassExtractor.reset();
//        for (OWLOntology ont : ontologies) {
//            for (OWLAxiom ax : ont.getAxioms(cls)) {
//                ax.accept(parentClassExtractor);
//            }
//        }
//        return new HashSet<OWLClass>(parentClassExtractor.getResult());
//    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////






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


        public void visit(OWLSubClassAxiom axiom) {
            axiom.getSuperClass().accept(extractor);
        }


        public void visit(OWLEquivalentClassesAxiom axiom) {
            for (OWLDescription desc : axiom.getDescriptions()) {
                if (desc.equals(current)) {
                    continue;
                }
                desc.accept(extractor);
            }
        }
    }


    private class NamedClassExtractor extends OWLDescriptionVisitorAdapter {

        Set<OWLClass> result = new HashSet<OWLClass>();


        public void reset() {
            result.clear();
        }


        public Set<OWLClass> getResult() {
            return result;
        }


        public void visit(OWLClass desc) {
            result.add(desc);
        }


        public void visit(OWLObjectIntersectionOf desc) {
            for (OWLDescription op : desc.getOperands()) {
                op.accept(this);
            }
        }
    }


    /**
     * Checks whether a class description contains a specified named conjunct.
     */
    private class NamedConjunctChecker extends OWLDescriptionVisitorAdapter {

        private boolean found;

        private OWLClass searchClass;


        public boolean containsConjunct(OWLClass conjunct, OWLDescription description) {
            found = false;
            searchClass = conjunct;
            description.accept(this);
            return found;
        }

        //////////////////////////////////////////////////////////////////////////////////////////


        public void visit(OWLClass desc) {
            if (desc.equals(searchClass)) {
                found = true;
            }
        }


        public void visit(OWLObjectIntersectionOf desc) {
            for (OWLDescription op : desc.getOperands()) {
                op.accept(this);
                if (found) {
                    break;
                }
            }
        }

        //////////////////////////////////////////////////////////////////////////////////////////
    }


    private class ChildClassExtractor extends OWLAxiomVisitorAdapter {


        private NamedConjunctChecker checker = new NamedConjunctChecker();

        private NamedClassExtractor namedClassExtractor = new NamedClassExtractor();

        private OWLClass currentParentClass;

        private Set<OWLClass> results = new HashSet<OWLClass>();


        public void reset() {
            results.clear();
            namedClassExtractor.reset();
        }


        public void setCurrentParentClass(OWLClass currentParentClass) {
            this.currentParentClass = currentParentClass;
//            checker.setNamedConjunct(currentParentClass);
            reset();
        }


        public Set<OWLClass> getResult() {
            return results;
        }


        public void visit(OWLSubClassAxiom axiom) {
            // Example:
            // If searching for subs of B, candidates are:
            // SubClassOf(A B)
            // SubClassOf(A And(B ...))
            if (checker.containsConjunct(currentParentClass, axiom.getSuperClass())) {
                // We only want named classes
                if (!axiom.getSubClass().isAnonymous()) {
                    results.add((OWLClass) axiom.getSubClass());
                }
            }
        }


        public void visit(OWLEquivalentClassesAxiom axiom) {
            // EquivalentClasses(A  And(B...))
            if (!namedClassInEquivalentAxiom(axiom)){
                return;
            }
            Set<OWLDescription> candidateDescriptions = new HashSet<OWLDescription>();
            boolean found = false;
            for (OWLDescription equivalentClass : axiom.getDescriptions()) {
                if (!checker.containsConjunct(currentParentClass, equivalentClass)) {
                    // Potential operand
                    candidateDescriptions.add(equivalentClass);
                }
                else {
                    // This axiom is relevant
                    if (equivalentClass.isAnonymous()) {
                        found = true;
                    }
                }
            }
            if (!found) {
                return;
            }
            namedClassExtractor.reset();
            for (OWLDescription desc : candidateDescriptions) {
                desc.accept(namedClassExtractor);
            }
            results.addAll(namedClassExtractor.getResult());
        }


        private boolean namedClassInEquivalentAxiom(OWLEquivalentClassesAxiom axiom) {
            for (OWLDescription equiv : axiom.getDescriptions()){
                if (!equiv.isAnonymous()){
                    return true;
                }
            }
            return false;
        }
    }
}
