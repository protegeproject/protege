package org.protege.editor.owl.model.hierarchy;

import org.protege.editor.core.Disposable;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 01-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * An interface to an object that can provide a hierarchy of objects, for
 * example a class, property or individual hierarchy.
 */
public interface OWLObjectHierarchyProvider<N extends OWLObject> extends Disposable, HasFilter<N> {


    /**
     * Sets the ontologies that this hierarchy provider should use
     * in order to determine the hierarchy.
     */
    public void setOntologies(Set<OWLOntology> ontologies);


    /**
     * Gets the objects that represent the roots of the hierarchy.
     */
    public Set<N> getRoots();


    public Set<N> getChildren(N object);


    public Set<N> getDescendants(N object);


    public Set<N> getParents(N object);


    public Set<N> getAncestors(N object);


    public Set<N> getEquivalents(N object);


    public Set<List<N>> getPathsToRoot(N object);


    public boolean containsReference(N object);


    public void addListener(OWLObjectHierarchyProviderListener<N> listener);


    public void removeListener(OWLObjectHierarchyProviderListener<N> listener);

    void dispose(); // override as previous implementations did not implement Disposable and did not throw an exception

    @Override
    default void setFilter(Predicate<N> filter) {
        // Do nothing
    }

    @Override
    default void clearFilter() {
        // Do nothing
    }

    @Override
    default Predicate<N> getFilter() {
        // No filtering - everything gets through.
        return n -> true;
    }

}
