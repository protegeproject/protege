package org.protege.editor.owl.model.hierarchy.cls;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.hierarchy.AbstractOWLObjectHierarchyProvider;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 06-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class InferredOWLClassHierarchyProvider extends AbstractOWLObjectHierarchyProvider<OWLClass> {

	/*
	 * There is no local state in this class - all the state is held in the reasoner and the ontologies on which
	 * this works.  But there is one race condition that I don't know how to track here.  The reasoner can be changed
	 * underneath this provider while it is running.  A listener doesn't really help because the reasoner can be changed 
	 * at any time.  But I can hope that the new reasoner will run the same way the old one did. 
	 */
	
    private final OWLModelManager owlModelManager;

    private final OWLClass owlThing;
    private final OWLClass owlNothing;

    private OWLModelManagerListener owlModelManagerListener = event -> {
        if (event.isType(EventType.REASONER_CHANGED) || event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED)
                || event.isType(EventType.ONTOLOGY_CLASSIFIED) || event.isType(EventType.ONTOLOGY_RELOADED)) {
            fireHierarchyChanged();
        }
    };

    public InferredOWLClassHierarchyProvider(OWLModelManager owlModelManager, OWLOntologyManager owlOntologyManager) {
        super(owlOntologyManager);
        this.owlModelManager = owlModelManager;

        owlThing = owlModelManager.getOWLDataFactory().getOWLThing();
        owlNothing = owlModelManager.getOWLDataFactory().getOWLNothing();

        owlModelManager.addListener(owlModelManagerListener);
    }


    public void rebuild() {
    }


    public void dispose() {
        super.dispose();
        owlModelManager.removeListener(owlModelManagerListener);
    }


    public Set<OWLClass> getRoots() {
        return Collections.singleton(owlThing);
    }


    protected OWLReasoner getReasoner() {
        return owlModelManager.getOWLReasonerManager().getCurrentReasoner();
    }


    public Set<OWLClass> getUnfilteredChildren(OWLClass object) {
//    	getReadLock().lock();
    	try {
            if(!getReasoner().isConsistent()) {
                return Collections.emptySet();
            }
    		Set<OWLClass> subs = getReasoner().getSubClasses(object, true).getFlattened();
    		// Add in owl:Nothing if there are inconsistent classes
    		if (object.isOWLThing() && !owlModelManager.getReasoner().getUnsatisfiableClasses().isSingleton()) {
    			subs.add(owlNothing);
    		}
    		else if (object.isOWLNothing()) {
    			subs.addAll(getReasoner().getUnsatisfiableClasses().getEntitiesMinus(owlNothing));
    		}
    		else {
    			// Class which is not Thing or Nothing
    			subs.remove(owlNothing);
    			for (Iterator<OWLClass> it = subs.iterator(); it.hasNext();) {
    				if (!getReasoner().isSatisfiable(it.next())) {
    					it.remove();
    				}
    			}
    		}
    		return subs;
    	}
    	finally {
//    		getReadLock().unlock();
    	}
    }


    public Set<OWLClass> getDescendants(OWLClass object) {
//    	getReadLock().lock();
        try {
            if(!getReasoner().isConsistent()) {
                return Collections.emptySet();
            }
    			return getReasoner().getSubClasses(object, false).getFlattened();
    	}
    	finally {
//    		getReadLock().unlock();
    	}
    }


    	public Set<OWLClass> getParents(OWLClass object) {
//    		getReadLock().lock();
    		try {
                if(!getReasoner().isConsistent()) {
                    return Collections.emptySet();
                }
    			if (object.isOWLNothing()) {

    				return Collections.singleton(owlThing);
    			}
    			else if (!getReasoner().isSatisfiable(object)){
    				return Collections.singleton(owlNothing);
    			}
    			Set<OWLClass> parents = getReasoner().getSuperClasses(object, true).getFlattened();
    			parents.remove(object);
    			return parents;
    		}
    		finally {
//                getReadLock().unlock();
    		}
    	}


    public Set<OWLClass> getAncestors(OWLClass object) {
//    	getReadLock().lock();
    	try {
            if(!getReasoner().isConsistent()) {
                return Collections.emptySet();
            }
    		return getReasoner().getSuperClasses(object, false).getFlattened();
    	}
    	finally {
//    		getReadLock().unlock();
    	}
    }


    public Set<OWLClass> getEquivalents(OWLClass object) {
//        getReadLock().lock();
        try {
            if(!getReasoner().isConsistent()) {
                return Collections.emptySet();
            }
            if (!getReasoner().isSatisfiable(object)) {
                return Collections.emptySet();
            }
            return getReasoner().getEquivalentClasses(object).getEntitiesMinus(object);
        }
        finally {
//            getReadLock().unlock();
        }
    }


    public boolean containsReference(OWLClass object) {
        return false;
    }


    protected void addRoot(OWLClass object) {
    }


    protected void removeRoot(OWLClass object) {
    }


    protected Set<OWLClass> getOrphanRoots(OWLClass object) {
        return Collections.emptySet();
    }


    /**
     * Sets the ontologies that this hierarchy provider should use
     * in order to determine the hierarchy.
     */
    public void setOntologies(Set<OWLOntology> ontologies) {
    }
}
