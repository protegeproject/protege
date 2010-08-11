package org.protege.editor.owl.model.hierarchy;

import org.semanticweb.owlapi.model.*;

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
public abstract class AbstractOWLPropertyHierarchyProvider<E extends OWLPropertyExpression<? extends OWLPropertyRange, E>, P extends E> extends AbstractOWLObjectHierarchyProvider<P> {

//    private static final Logger logger = Logger.getLogger(AbstractOWLPropertyHierarchyProvider.class);

    private Set<OWLOntology> ontologies;

    private Set<P> subPropertiesOfRoot;

    private OWLOntologyChangeListener listener;


    public AbstractOWLPropertyHierarchyProvider(OWLOntologyManager owlOntologyManager) {
        super(owlOntologyManager);
        this.subPropertiesOfRoot = new HashSet<P>();
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
        this.ontologies.clear();
        this.ontologies.addAll(ontologies);
        rebuildRoots();
        fireHierarchyChanged();
    }


    public boolean containsReference(P object) {
        for (OWLOntology ont : ontologies) {
            if (getReferencedProperties(ont).contains(object)) {
                return true;
            }
        }
        return false;
    }


    public Set<P> getChildren(P object) {
        if (object.equals(getRoot())){
            return Collections.unmodifiableSet(subPropertiesOfRoot);
        }

        final Set<P> result = new HashSet<P>();
        for (E subProp : object.getSubProperties(ontologies)){
            // Don't add the sub property if it is a parent of
            // itself - i.e. prevent cycles
            if (!subProp.isAnonymous() &&
                !getAncestors((P)subProp).contains(subProp)) {
                result.add((P)subProp);
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

        for (E prop : object.getEquivalentProperties(ontologies)) {
            if (!prop.isAnonymous()) {
                result.add((P)prop);
            }
        }

        result.remove(object);
        return result;
    }


    public Set<P> getParents(P object) {
        if (object.equals(getRoot())){
            return Collections.emptySet();
        }

        Set<P> result = new HashSet<P>();
        for (E prop : object.getSuperProperties(ontologies)) {
            if (!prop.isAnonymous()) {
                result.add((P)prop);
            }
        }
        if (result.isEmpty()){
            result.add(getRoot());
        }

        return result;
    }
}
