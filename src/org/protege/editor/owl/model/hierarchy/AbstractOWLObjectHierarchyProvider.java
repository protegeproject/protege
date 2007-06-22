package org.protege.editor.owl.model.hierarchy;

import org.apache.log4j.Logger;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntologyManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private static final Logger logger = Logger.getLogger(AbstractOWLObjectHierarchyProvider.class);


    private boolean fireEvents;

    private List<OWLObjectHierarchyProviderListener<N>> listeners;

    private OWLOntologyManager manager;


    protected AbstractOWLObjectHierarchyProvider(OWLOntologyManager owlOntologyManager) {
        this.manager = owlOntologyManager;
        listeners = new ArrayList<OWLObjectHierarchyProviderListener<N>>();
        fireEvents = true;
    }


    public OWLOntologyManager getManager() {
        return manager;
    }


    public void dispose() {
        listeners.clear();
    }


    public Set<N> getAncestors(N object) {
        Set<N> results = new HashSet<N>();
        getAncestors(results, object);
        return results;
    }


    private void getAncestors(Set<N> results, N object) {
        for (N parent : getParents(object)) {
            if (!results.contains(parent)) {
                results.add(parent);
                getAncestors(results, parent);
            }
        }
    }


    public Set<N> getDescendants(N object) {
        Set<N> results = new HashSet<N>();
        getDescendants(results, object);
        return results;
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
        return setOfPaths(obj, new HashSet<N>());
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


    private Set<N> createSet() {
        return new HashSet<N>();
    }


    protected void setFireEvents(boolean b) {
        fireEvents = b;
    }


    public void addListener(OWLObjectHierarchyProviderListener<N> listener) {
        listeners.add(listener);
    }


    public void removeListener(OWLObjectHierarchyProviderListener<N> listener) {
        listeners.remove(listener);
    }


    protected void fireNodeChanged(N node) {
        if (!fireEvents) {
            return;
        }
        for (OWLObjectHierarchyProviderListener<N> listener : new ArrayList<OWLObjectHierarchyProviderListener<N>>(
                listeners)) {
            try {
                listener.nodeChanged(node);
            }
            catch (Throwable e) {
                e.printStackTrace();
                logger.warn(getClass().getName() + ": Listener" + listener + " has thrown an exception.  Removing bad listener!");
                listeners.remove(listener);
                throw new RuntimeException(e);
            }
        }
    }


    protected void fireHierarchyChanged() {
        if (!fireEvents) {
            return;
        }
        for (OWLObjectHierarchyProviderListener<N> listener : new ArrayList<OWLObjectHierarchyProviderListener<N>>(
                listeners)) {
            try {
                listener.hierarchyChanged();
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
