package org.protege.editor.owl.model.hierarchy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.protege.owlapi.inference.cls.ChildClassExtractor;
import org.protege.owlapi.inference.cls.ParentClassExtractor;
import org.protege.owlapi.inference.orphan.Relation;
import org.protege.owlapi.inference.orphan.TerminalElementFinder;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomChange;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.search.EntitySearcher;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 17-Jan-2007<br><br>
 */
public class AssertedClassHierarchyProvider extends AbstractOWLObjectHierarchyProvider<OWLClass> {

    private OWLOntologyManager owlOntologyManager;
    
    private ReadLock ontologySetReadLock;
    private WriteLock ontologySetWriteLock;

	/*
	 * The ontologies variable is protected by the ontologySetReadLock and the ontologySetWriteLock.
	 * These locks are always taken and held inside of the getReadLock() and getWriteLock()'s for the
	 * OWL Ontology Manager.  This is necessary because when the set of ontologies changes, everything
	 * about this class changes.  So when the set of ontologies is changed we need to make sure that nothing
	 * else is running.
	 */
    /*
     * It is not safe to set the collection of ontologies to a HashSet or TreeSet.  
     * When an ontology changes name it gets a new Hash Code and it is sorted 
     * differently, so these Collections do not work.
     */
    private Collection<OWLOntology> ontologies;

    private volatile OWLClass root;

    private ParentClassExtractor parentClassExtractor;

    private ChildClassExtractor childClassExtractor;

    private OWLOntologyChangeListener listener;

    private TerminalElementFinder<OWLClass> rootFinder;

    private Set<OWLClass> nodesToUpdate = new HashSet<OWLClass>();


    public AssertedClassHierarchyProvider(OWLOntologyManager owlOntologyManager) {
        super(owlOntologyManager);
        this.owlOntologyManager = owlOntologyManager;
        /*
         * It is not safe to set the collection of ontologies to a HashSet or TreeSet.  
         * When an ontology changes name it gets a new Hash Code and it is sorted 
         * differently, so these Collections do not work.
         */
        ontologies = new ArrayList<OWLOntology>();
        ReentrantReadWriteLock locks = new ReentrantReadWriteLock();
        ontologySetReadLock = locks.readLock();
        ontologySetWriteLock = locks.writeLock();
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
    	getReadLock().lock();
    	ontologySetWriteLock.lock();
    	try {
    		/*
    		 * It is not safe to set the collection of ontologies to a HashSet or TreeSet.  
    		 * When an ontology changes name it gets a new Hash Code and it is sorted 
    		 * differently, so these Collections do not work.
    		 */
    		this.ontologies = new ArrayList<OWLOntology>(ontologies);
    		nodesToUpdate.clear();
    		if (root == null) {
    			root = owlOntologyManager.getOWLDataFactory().getOWLThing();
    		}
    		rebuildImplicitRoots();
    		fireHierarchyChanged();
    	}
    	finally {
    		ontologySetWriteLock.unlock();
    		getReadLock().unlock();
    	}
    }


    private void rebuildImplicitRoots() {
    	getReadLock().lock();
    	ontologySetReadLock.lock();
    	try {
        rootFinder.clear();
        for (OWLOntology ont : ontologies) {
            Set<OWLClass> ref = ont.getClassesInSignature();
            rootFinder.appendTerminalElements(ref);
        }
        rootFinder.finish();
    	}
    	finally {
    		ontologySetReadLock.unlock();
    		getReadLock().unlock();
    	}
    }

    public void dispose() {
        getManager().removeOntologyChangeListener(listener);
    }


    /*
     * This call holds the write lock so no other thread can hold the either the OWL ontology 
     * manager read or write locks or the ontologies 
     */
    private void handleChanges(List<? extends OWLOntologyChange> changes) {
        Set<OWLClass> oldTerminalElements = new HashSet<OWLClass>(rootFinder.getTerminalElements());
        Set<OWLClass> changedClasses = new HashSet<OWLClass>();
        changedClasses.add(root);
        List<OWLAxiomChange> filteredChanges = filterIrrelevantChanges(changes);
        updateImplicitRoots(filteredChanges);
        for (OWLOntologyChange change : filteredChanges) {
        	for (OWLEntity entity : ((OWLAxiomChange) change).getSignature()) {
        		if (entity instanceof OWLClass && !entity.equals(root)) {
        			changedClasses.add((OWLClass) entity);
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
    
    private List<OWLAxiomChange> filterIrrelevantChanges(List<? extends OWLOntologyChange> changes) {
    	List<OWLAxiomChange> filteredChanges = new ArrayList<OWLAxiomChange>();
    	for (OWLOntologyChange change : changes) {
    		// only listen for changes on the appropriate ontologies
    		if (ontologies.contains(change.getOntology())){
    			if (change.isAxiomChange()) {
    				filteredChanges.add((OWLAxiomChange) change);
    			}
    		}
    	}
    	return filteredChanges;
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


    private void updateImplicitRoots(List<OWLAxiomChange> changes) {
    	Set<OWLClass> possibleTerminalElements = new HashSet<OWLClass>();
    	Set<OWLClass> notInOntologies = new HashSet<OWLClass>();
    	
    	for (OWLOntologyChange change : changes) {
    		// only listen for changes on the appropriate ontologies
    		if (ontologies.contains(change.getOntology())){
    			if (change.isAxiomChange()) {
    				boolean remove = change instanceof RemoveAxiom;
    				OWLAxiom axiom = change.getAxiom();

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
    			}
    		}
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
    	getReadLock().lock();
    	ontologySetReadLock.lock();
    	try {
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
    	finally {
    		ontologySetReadLock.unlock();
    		getReadLock().unlock();
    	}
    }


    private Set<OWLClass> extractChildren(OWLClass parent) {
        childClassExtractor.setCurrentParentClass(parent);
        for (OWLOntology ont : ontologies) {
            for (OWLAxiom ax : ont.getReferencingAxioms(parent)) {
                if (ax.isLogicalAxiom()) {
                    ax.accept(childClassExtractor);
                }
            }
        }
        return childClassExtractor.getResult();
    }


    public boolean containsReference(OWLClass object) {
    	getReadLock().lock();
    	ontologySetReadLock.lock();
    	try {
    		for (OWLOntology ont : ontologies) {
    			if (ont.containsClassInSignature(object.getIRI())) {
    				return true;
    			}
    		}
    		return false;
    	}
    	finally {
    		ontologySetReadLock.unlock();
    		getReadLock().unlock();
    	}
    }


    public Set<OWLClass> getParents(OWLClass object) {
    	getReadLock().lock();
    	ontologySetReadLock.lock();
    	try {
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
    	finally {
    		ontologySetReadLock.unlock();
    		getReadLock().unlock();
    	}
    }


    public Set<OWLClass> getEquivalents(OWLClass object) {
    	getReadLock().lock();
    	ontologySetReadLock.lock();
    	try {
    		Set<OWLClass> result = new HashSet<OWLClass>();
    		for (OWLOntology ont : ontologies) {
    			for (OWLClassExpression equiv : EntitySearcher.getEquivalentClasses(object,ont)) {
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
    	finally {
    		ontologySetReadLock.unlock();
    		getReadLock().unlock();
    	}
    }

}
