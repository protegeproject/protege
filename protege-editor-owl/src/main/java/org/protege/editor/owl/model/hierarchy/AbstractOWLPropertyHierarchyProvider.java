package org.protege.editor.owl.model.hierarchy;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.search.EntitySearcher;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 23-Jan-2007<br><br>
 */
public abstract class AbstractOWLPropertyHierarchyProvider<R extends OWLPropertyRange, E extends OWLPropertyExpression, P extends E> extends AbstractOWLObjectHierarchyProvider<P> {

//    private static final Logger logger = LoggerFactory.getLogger(AbstractOWLPropertyHierarchyProvider.class);
	
	private ReadLock ontologySetReadLock;
	private WriteLock ontologySetWriteLock;

	/*
	 * The ontologies variable is protected by the ontologySetReadLock and the ontologySetWriteLock.
	 * These locks are always taken and held inside of the getReadLock() and getWriteLock()'s for the
	 * OWL Ontology Manager.  This is necessary because when the set of ontologies changes, everything
	 * about this class changes.  So when the set of ontologies is changed we need to make sure that nothing
	 * else is running.
	 */
    private Set<OWLOntology> ontologies;

    private Set<P> subPropertiesOfRoot;

    private OWLOntologyChangeListener listener;


    public AbstractOWLPropertyHierarchyProvider(OWLOntologyManager owlOntologyManager) {
        super(owlOntologyManager);
        this.subPropertiesOfRoot = new HashSet<P>();
        ontologies = new FakeSet<OWLOntology>();
        ReentrantReadWriteLock locks = new ReentrantReadWriteLock();
        ontologySetReadLock = locks.readLock();
        ontologySetWriteLock = locks.writeLock();
        listener = new OWLOntologyChangeListener() {
            /**
             * Called when some changes have been applied to various ontologies.  These
             * may be an axiom added or an axiom removed changes.
             * @param changes A list of changes that have occurred.  Each change may be examined
             *                to determine which ontology it was applied to.
             */
            public void ontologiesChanged(List<? extends OWLOntologyChange> changes) {
                handleChanges(changes);
            }
        };
        owlOntologyManager.addOntologyChangeListener(listener);
    }


    public void dispose() {
        super.dispose();
        getManager().removeOntologyChangeListener(listener);
    }


    /*
     * This call holds the write lock so no other thread can hold the either the OWL ontology 
     * manager read or write locks or the ontologies 
     */
    private void handleChanges(List<? extends OWLOntologyChange> changes) {
        Set<P> properties = new HashSet<P>(getPropertiesReferencedInChange(changes));
        for (P prop : properties) {
            if (isSubPropertyOfRoot(prop)) {
                subPropertiesOfRoot.add(prop);
                fireNodeChanged(getRoot());
            }
            else {
                if (getAncestors(prop).contains(prop)) {
                    subPropertiesOfRoot.add(prop);
                    for (P anc : getAncestors(prop)) {
                        if (getAncestors(anc).contains(prop)) {
                            subPropertiesOfRoot.add(anc);
                            fireNodeChanged(anc);
                        }
                    }
                }
                else {
                    subPropertiesOfRoot.remove(prop);
                }
            }
            fireNodeChanged(prop);
        }
        fireNodeChanged(getRoot());
    }


    protected abstract Set<P> getPropertiesReferencedInChange(List<? extends OWLOntologyChange> changes);


    private boolean isSubPropertyOfRoot(P prop) {

        if (prop.equals(getRoot())){
            return false;
        }

        // We deem a property to be a sub of the top property if this is asserted
        // or if no named superproperties are asserted
        final Set<P> parents = getParents(prop);
        if (parents.isEmpty() || parents.contains(getRoot())){
            for (OWLOntology ont : ontologies) {
                if (containsReference(ont, prop)) {
                    return true;
                }
            }
        }
        // Additional condition: If we have  P -> Q and Q -> P, then
        // there is no path to the root, so put P and Q as root properties
        // Collapse any cycles and force properties that are equivalent
        // through cycles to appear at the root.
        return getAncestors(prop).contains(prop);
    }


    private void rebuildRoots() {
        subPropertiesOfRoot.clear();
        for (OWLOntology ontology : ontologies) {
            for (P prop : getReferencedProperties(ontology)) {
                if (isSubPropertyOfRoot(prop)) {
                    subPropertiesOfRoot.add(prop);
                }
            }
        }
    }


    protected abstract boolean containsReference(OWLOntology ont, P prop);


    /**
     * Gets the relevant properties in the specified ontology that are contained
     * within the property hierarchy.  For example, for an object property hierarchy
     * this would constitute the set of referenced object properties in the specified
     * ontology.
     * @param ont The ontology
     */
    protected abstract Set<? extends P> getReferencedProperties(OWLOntology ont);


    protected abstract Set<? extends OWLSubPropertyAxiom> getSubPropertyAxiomForRHS(P prop, OWLOntology ont);


    protected abstract P getRoot();


    /**
     * Gets the objects that represent the roots of the hierarchy.
     */
    public Set<P> getRoots() {
        return Collections.singleton(getRoot());
    }


    /**
     * Sets the ontologies that this hierarchy provider should use
     * in order to determine the hierarchy.
     */
    final public void setOntologies(Set<OWLOntology> ontologies) {
//    	getReadLock().lock();
    	ontologySetWriteLock.lock();
    	try {
    		this.ontologies.clear();
    		this.ontologies.addAll(ontologies);
    		rebuildRoots();
    		fireHierarchyChanged();
    	}
    	finally {
    		ontologySetWriteLock.unlock();
//    		getReadLock().unlock();
    	}
    }


    public boolean containsReference(P object) {
//    	getReadLock().lock();
    	ontologySetReadLock.lock();
    	try {
    		for (OWLOntology ont : ontologies) {
    			if (getReferencedProperties(ont).contains(object)) {
    				return true;
    			}
    		}
    		return false;
    	}
    	finally {
    		ontologySetReadLock.unlock();
//    		getReadLock().unlock();
    	}
    }


    public Set<P> getChildren(P object) {
//    	getReadLock().lock();
    	ontologySetReadLock.lock();
    	try {
    		if (object.equals(getRoot())){
    			return Collections.unmodifiableSet(subPropertiesOfRoot);
    		}

    		final Set<P> result = new HashSet<P>();
    		for (E subProp : EntitySearcher.getSubProperties(object, ontologies)){
    			// Don't add the sub property if it is a parent of
    			// itself - i.e. prevent cycles
    			if (!subProp.isAnonymous() &&
    					!getAncestors((P)subProp).contains(subProp)) {
    				result.add((P)subProp);
    			}
    		}
    		return result;
    	}
    	finally {
    		ontologySetReadLock.unlock();
//    		getReadLock().unlock();
    	}
    }


    public Set<P> getEquivalents(P object) {
//    	getReadLock().lock();
    	ontologySetReadLock.lock();
    	try {
    		Set<P> result = new HashSet<P>();
    		Set<P> ancestors = getAncestors(object);
    		if (ancestors.contains(object)) {
    			for (P anc : ancestors) {
    				if (getAncestors(anc).contains(object)) {
    					result.add(anc);
    				}
    			}
    		}

    		for (E prop : EntitySearcher.getEquivalentProperties(object, ontologies)) {
    			if (!prop.isAnonymous()) {
    				result.add((P)prop);
    			}
    		}

    		result.remove(object);
    		return result;
    	}
    	finally {
    		ontologySetReadLock.unlock();
//    		getReadLock().unlock();
    	}
    }


    public Set<P> getParents(P object) {
//    	getReadLock().lock();
    	ontologySetReadLock.lock();
    	try {
    		if (object.equals(getRoot())){
    			return Collections.emptySet();
    		}

    		Set<P> result = new HashSet<>();
    		for (E prop : EntitySearcher.getSuperProperties(object, ontologies)) {
    			if (!prop.isAnonymous()) {
    				result.add((P) prop);
    			}
    		}
    		if (result.isEmpty() && isReferenced(object)){
    			result.add(getRoot());
    		}

    		return result;
    	}
    	finally {
    		ontologySetReadLock.unlock();
//    		getReadLock().unlock();
    	}
    }
    
    
    private boolean isReferenced(P e) {
    	return e.accept(new IsReferencePropertyExpressionVisitor());
    }
    
    private class IsReferencePropertyExpressionVisitor implements OWLPropertyExpressionVisitorEx<Boolean> {

		@Override
		public Boolean visit(OWLAnnotationProperty owlAnnotationProperty) {
			return isReferenced(owlAnnotationProperty);
		}

		public Boolean visit(OWLObjectProperty property) {
			return isReferenced(property);
		}

		public Boolean visit(OWLObjectInverseOf property) {
			return property.getInverse().accept(this);
		}

		public Boolean visit(OWLDataProperty property) {
			return isReferenced(property);
		}
    	
    	
    	private boolean isReferenced(OWLEntity e) {
        	for (OWLOntology ontology : ontologies) {
        		if (ontology.containsEntityInSignature(e)) {
        			return true;
        		}
        	}
        	return false;
    	}
    }
    
    private class FakeSet<X> extends AbstractSet<X> {
    	private List<X> elements = new ArrayList<X>();
    	
    	@Override
    	public Iterator<X> iterator() {
    		return elements.iterator();
    	}
    	
    	@Override
    	public int size() {
    		return elements.size();
    	}
    	
    	@Override
    	public boolean add(X e) {
    		if (!elements.contains(e)) {
    			elements.add(e);
    			return true;
    		}
    		return false;
    	}
    	
    	
    	@Override
    	public void clear() {
    		elements.clear();
    	}
    }
}
