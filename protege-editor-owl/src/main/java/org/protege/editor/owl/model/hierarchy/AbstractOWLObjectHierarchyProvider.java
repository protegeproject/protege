package org.protege.editor.owl.model.hierarchy;

import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toSet;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 01-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A basic partial implementation of a hierarchy provider, which
 * handles listeners and event firing, and also provides basic
 * implementations of method such as getAncestors, getDescendants etc.
 * which use other core methods.
 */
public abstract class AbstractOWLObjectHierarchyProvider<N extends OWLObject> implements OWLObjectHierarchyProvider<N> {

    private final Logger logger = LoggerFactory.getLogger(AbstractOWLObjectHierarchyProvider.class);
    
    private volatile boolean fireEvents;

    /*
     * The listeners object synchronizes the listeners data.
     */
    private List<OWLObjectHierarchyProviderListener<N>> listeners;

    private OWLOntologyManager manager;

    private Predicate<N> filter = n -> true;

    /*
     * If you expect this or any of its subclasses to be thread safe it must be a WriteSafeOWLOntologyManager.
     * Ideally we would change the interface here but this might break some existing plugin code.  On the other hand,
     * all Protege code will pass in a ProtegeOWLOntologyManager and Web-Protege will pass in another implementation of the 
     * WriteSafeOWLOntologyManager.
     */
    protected AbstractOWLObjectHierarchyProvider(OWLOntologyManager owlOntologyManager) {
//        if (!(owlOntologyManager instanceof WriteSafeOWLOntologyManager)) { // I know this is ugly but it fixes problems elsewhere...
//        	throw new IllegalStateException("Hierarchy providers must have a thread safe ontology mananger.");
//        }
        this.manager = owlOntologyManager;
        listeners = new ArrayList<>();
        fireEvents = true;
    }


    public OWLOntologyManager getManager() {
        return manager;
    }

    @Override
    public void setFilter(Predicate<N> filter) {
        this.filter = checkNotNull(filter);
        fireHierarchyChanged();
    }

    @Override
    public void clearFilter() {
        this.filter = n -> true;
        fireHierarchyChanged();
    }

    @Override
    public Predicate<N> getFilter() {
        return filter;
    }
//	protected ReentrantReadWriteLock getReadWriteLock() {
//		return manager.getReadWriteLock();
//	}
//
//	protected ReadLock getReadLock() {
//		return manager.getReadLock();
//	}
//
//	protected WriteLock getWriteLock() {
//		return manager.getWriteLock();
//	}


    public void dispose() {
    	synchronized (listeners) {
    		listeners.clear();
    	}
    }


    public Set<N> getAncestors(N object) {
//    	getReadLock().lock();
    	try {
    		Set<N> results = new HashSet<N>();
    		getAncestors(results, object);
    		return results;
    	}
    	finally {
//    		getReadLock().unlock();
    	}
    }


    private void getAncestors(Set<N> results, N object) {
        for (N parent : getParents(object)) {
            if (!results.contains(parent)) {
                results.add(parent);
                getAncestors(results, parent);
            }
        }
    }

    @Override
    public Set<N> getChildren(N object) {
        return getUnfilteredChildren(object)
                .stream()
                .filter(filter)
                .collect(toSet());
    }

    protected Set<N> getUnfilteredChildren(N object) {
        return Collections.emptySet();
    }

    public Set<N> getDescendants(N object) {
//    	getReadLock().lock();
    	try {
    		Set<N> results = new HashSet<N>();
    		getDescendants(results, object);
    		return results;
    	}
    	finally {
//    		getReadLock().unlock();
    	}
    }


    private void getDescendants(Set<N> results, N object) {
        for (N child : getChildren(object)) {
            if (!results.contains(child)) {
                results.add(child);
                getDescendants(results, child);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Paths to root stuff
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Gets the paths to the root class for the specified object.
     * @return A <code>Set</code> of <code>List</code>s of <code>N</code>s
     */
    public Set<List<N>> getPathsToRoot(N obj) {
//    	getReadLock().lock();
    	try { 	
    		return setOfPaths(obj, new HashSet<N>());
    	}
    	finally {
//    		getReadLock().unlock();
    	}
    }


    private Set<List<N>> setOfPaths(N obj, Set<N> processed) {
        if (getRoots().contains(obj)) {
            return getSingleSetOfLists(obj);
        }
        Set<List<N>> paths = new HashSet<List<N>>();
        for (N par : getParents(obj)) {
            if (!processed.contains(par)) {
                processed.add(par);
                paths.addAll(append(obj, setOfPaths(par, processed)));
            }
        }
        return paths;
    }


    private Set<List<N>> getSingleSetOfLists(N obj) {
        Set<List<N>> set = new HashSet<List<N>>();
        List<N> list = new ArrayList<N>();
        list.add(obj);
        set.add(list);
        return set;
    }


    private Set<List<N>> append(N obj, Set<List<N>> setOfPaths) {
        for (List<N> path : setOfPaths) {
            path.add(obj);
        }
        return setOfPaths;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////


    protected void setFireEvents(boolean b) {
        fireEvents = b;
    }


    public void addListener(OWLObjectHierarchyProviderListener<N> listener) {
    	synchronized (listeners) {
    		listeners.add(listener);
    	}
    }


    public void removeListener(OWLObjectHierarchyProviderListener<N> listener) {
    	synchronized (listeners) {
    		listeners.remove(listener);
    	}
    }

    private List<OWLObjectHierarchyProviderListener<N>> getListeners() {
    	synchronized (listeners) {
    		return new ArrayList<OWLObjectHierarchyProviderListener<N>>(listeners);
    	}
    }

    protected void fireNodeChanged(N node) {
        if (!fireEvents) {
            return;
        }
        for (OWLObjectHierarchyProviderListener<N> listener : getListeners()) {
            try {
                listener.nodeChanged(node);
            }
            catch (Throwable e) {
                e.printStackTrace();
                logger.error("{}: Listener {} has thrown an exception.  Removing bad listener.",
                        getClass().getName(),
                        listener);
                removeListener(listener);
                throw new RuntimeException(e);
            }
        }
    }


    protected void fireHierarchyChanged() {
        if (!fireEvents) {
            return;
        }
        for (OWLObjectHierarchyProviderListener<N> listener : getListeners()) {
            try {
                listener.hierarchyChanged();
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
