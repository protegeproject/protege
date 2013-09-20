package org.protege.editor.owl.model.hierarchy.cls;

import java.util.Collections;
import java.util.Set;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.hierarchy.AbstractSuperClassHierarchyProvider;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 14-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class InferredSuperClassHierarchyProvider extends AbstractSuperClassHierarchyProvider {

    private OWLReasoner reasoner;


    public InferredSuperClassHierarchyProvider(OWLModelManager manager) {
        super(manager.getOWLOntologyManager());
    }


    public void setReasoner(OWLReasoner reasoner) {
        this.reasoner = reasoner;
        fireHierarchyChanged();
    }


    protected Set<? extends OWLClassExpression> getEquivalentClasses(OWLClass cls) {
    	OWLReasoner myReasoner = reasoner;
        // Get the equivalent classes from the reasoner
        if (myReasoner == null) {
            return Collections.emptySet();
        }
        if (!myReasoner.isSatisfiable(cls)) {
            // We don't want every class in the ontology
            return Collections.emptySet();
        }
        return myReasoner.getEquivalentClasses(cls).getEntities();
    }


    public Set<OWLClass> getChildren(OWLClass object) {
    	OWLReasoner myReasoner = reasoner;
    	getReadLock().lock();
    	try {
    		// Simply get the superclasses from the reasoner
    		if (myReasoner == null) {
    			return Collections.emptySet();
    		}
    		if (!myReasoner.isSatisfiable(object)) {
    			// We don't want every class in the ontology!!
    			return Collections.emptySet();
    		}
    		return myReasoner.getSuperClasses(object, true).getFlattened();
    	}
    	finally {
    		getReadLock().unlock();
    	}
    }


    public Set<OWLClass> getEquivalents(OWLClass object) {
        return Collections.emptySet();
    }


    public Set<OWLClass> getParents(OWLClass object) {
    	OWLReasoner myReasoner = reasoner;
    	getReadLock().lock();
    	try {
    		// Simply get the superclasses from the reasoner
    		if (myReasoner == null) {
    			return Collections.emptySet();
    		}
    		if (!myReasoner.isSatisfiable(object)) {
    			// We don't want every class in the ontology!!
    			return Collections.emptySet();
    		}
    		return myReasoner.getSubClasses(object, true).getFlattened();
    	}
    	finally {
    		getReadLock().unlock();
    	}
    }


    public boolean containsReference(OWLClass object) {
        return false;
    }


    /**
     * Sets the ontologies that this hierarchy provider should use
     * in order to determine the hierarchy.
     */
    public void setOntologies(Set<OWLOntology> ontologies) {
    }
}
