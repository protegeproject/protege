package org.protege.editor.owl.model.hierarchy;

import org.apache.log4j.Logger;
import org.semanticweb.owl.model.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 23-Jan-2007<br><br>
 */
public abstract class AbstractOWLPropertyHierarchyProvider<E extends OWLPropertyExpression<E, ? extends OWLPropertyRange>, P extends E> extends AbstractOWLObjectHierarchyProvider<P> {

    private static final Logger logger = Logger.getLogger(AbstractOWLPropertyHierarchyProvider.class);


    private Set<OWLOntology> ontologies;

    private Set<P> roots;

    private OWLOntologyChangeListener listener;


    public AbstractOWLPropertyHierarchyProvider(OWLOntologyManager owlOntologyManager) {
        super(owlOntologyManager);
        this.roots = new HashSet<P>();
        ontologies = new HashSet<OWLOntology>();
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


    private void handleChanges(List<? extends OWLOntologyChange> changes) {
        Set<P> properties = new HashSet<P>(getPropertiesReferencedInChange(changes));
        for (P prop : properties) {
            if (isRoot(prop)) {
                roots.add(prop);
            }
            else {
                if (getAncestors(prop).contains(prop)) {
                    roots.add(prop);
                    for (P anc : getAncestors(prop)) {
                        if (getAncestors(anc).contains(prop)) {
                            roots.add(anc);
                            fireNodeChanged(anc);
                        }
                    }
                }
                else {
                    roots.remove(prop);
                }
            }
            fireNodeChanged(prop);
        }
    }


    protected abstract Set<P> getPropertiesReferencedInChange(List<? extends OWLOntologyChange> changes);


    private boolean isRoot(P prop) {

        // We deem a property to be a root property if it doesn't have
        // any super properties (i.e. it is not on
        // the LHS of a subproperty axiom
        // Assume the property is a root property to begin with
        boolean isRoot = getParents(prop).isEmpty();
        boolean isReferenced = false;
        for (OWLOntology ont : ontologies) {
            if (containsReference(ont, prop)) {
                isReferenced = true;
            }
        }
        if (isRoot && isReferenced) {
            return true;
        }
        else {
            // Additional condition: If we have  P -> Q and Q -> P, then
            // there is no path to the root, so put P and Q as root properties
            // Collapse any cycles and force properties that are equivalent
            // through cycles to appear at the root.
            return getAncestors(prop).contains(prop);
        }
    }


    private void rebuildRoots() {
        roots.clear();
        for (OWLOntology ontology : ontologies) {
            for (P prop : getReferencedProperties(ontology)) {
                if (isRoot(prop)) {
                    roots.add(prop);
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


    /**
     * Gets the objects that represent the roots of the hierarchy.
     */
    public Set<P> getRoots() {
        return Collections.unmodifiableSet(roots);
    }


    /**
     * Sets the ontologies that this hierarchy provider should use
     * in order to determine the hierarchy.
     */
    final public void setOntologies(Set<OWLOntology> ontologies) {
        this.ontologies.clear();
        this.ontologies.addAll(ontologies);
        rebuildRoots();
        fireHierarchyChanged();
    }


    protected abstract Set<? extends OWLSubPropertyAxiom> getSubPropertyAxiomForRHS(P prop, OWLOntology ont);


    public boolean containsReference(P object) {
        for (OWLOntology ont : ontologies) {
            if (getReferencedProperties(ont).contains(object)) {
                return true;
            }
        }
        return false;
    }


    public Set<P> getChildren(P object) {
        Set<P> result = new HashSet<P>();
        for (OWLOntology ontology : ontologies) {
            for (OWLSubPropertyAxiom<P> ax : getSubPropertyAxiomForRHS(object, ontology)) {
                if (!ax.getSubProperty().isAnonymous()) {
                    P subProp = ax.getSubProperty();
                    // Don't add the sub property if it is a parent of
                    // itself - i.e. prevent cycles
                    if (!getAncestors(subProp).contains(subProp)) {
                        result.add(subProp);
                    }
                }
            }
        }
        return result;
    }


    public Set<P> getEquivalents(P object) {
        Set<P> result = new HashSet<P>();
        Set<P> ancestors = getAncestors(object);
        if (ancestors.contains(object)) {
            for (P anc : ancestors) {
                if (getAncestors(anc).contains(object)) {
                    result.add(anc);
                }
            }
        }
        for (OWLOntology ont : ontologies) {
            for (E prop : object.getEquivalentProperties(ont)) {
                if (!prop.isAnonymous()) {
                    result.add((P) prop);
                }
            }
        }
        result.remove(object);
        return result;
    }


    public Set<P> getParents(P object) {
        Set<P> result = new HashSet<P>();
        for (OWLOntology ontology : ontologies) {
            for (E prop : object.getSuperProperties(ontology)) {
                if (!prop.isAnonymous()) {
                    result.add((P) prop);
                }
            }
        }
        return result;
    }
}
