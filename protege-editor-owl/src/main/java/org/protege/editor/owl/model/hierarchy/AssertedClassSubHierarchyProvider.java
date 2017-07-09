package org.protege.editor.owl.model.hierarchy;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.parameters.Imports;

public class AssertedClassSubHierarchyProvider extends AssertedClassHierarchyProvider {

	public AssertedClassSubHierarchyProvider(OWLOntologyManager owlOntologyManager) {
		super(owlOntologyManager);		
	}
	
	public void setRoot(OWLClass r) { 
		root = r;
		this.rebuildImplicitRoots();
	}
	
	public Set<OWLClass> getParents(OWLClass object) {
//    	getReadLock().lock();
        ontologySetReadLock.lock();
        try {
        	// If the object is thing then there are no
        	// parents
        	if (object.equals(root)) {
        		return Collections.emptySet();
        	}
        	Set<OWLClass> result = new HashSet<>();
        	// Thing if the object is a root class
        	if (rootFinder.getTerminalElements().contains(object)) {
        		result.add(root);
        	}
        	// Not a root, so must have another parent
        	parentClassExtractor.reset();
        	parentClassExtractor.setCurrentClass(object);
        	for (OWLOntology ont : ontologies) {
        		for (OWLAxiom ax : ont.getAxioms(object, Imports.EXCLUDED)) {
        			ax.accept(parentClassExtractor);
        		}
        	}
        	Set<OWLClass> ps = parentClassExtractor.getResult();
        	if (ps.contains(root)) {
        		result.add(root);
        	} else {
        		result.addAll(ps);
        	}
        	return result;
        } finally {
            ontologySetReadLock.unlock();
//    		getReadLock().unlock();
        }
    }
	
	protected Set<OWLClass> getUnfilteredChildren(OWLClass object) {
        //    	getReadLock().lock();
        ontologySetReadLock.lock();
        try {
        	// check if we are beginning with Thing. If so that the
        	// terminal elements of the root finder, which have no parents,
        	// are what we want
            if (object.equals(owlOntologyManager.getOWLDataFactory().getOWLThing())) {
                Set<OWLClass> result = new HashSet<>();
                result.addAll(rootFinder.getTerminalElements());
                result.addAll(extractChildren(object));
                result.remove(object);
                return result;
            }
            else {
                Set<OWLClass> result = extractChildren(object);
                for (Iterator<OWLClass> it = result.iterator(); it.hasNext(); ) {
                    OWLClass curChild = it.next();
                    if (getAncestors(object).contains(curChild)) {
                        it.remove();
                    }
                }
                return result;
            }
        } finally {
            ontologySetReadLock.unlock();
//    		getReadLock().unlock();
        }
    }
}
