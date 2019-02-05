package org.protege.editor.owl.model.hierarchy;

import com.google.common.collect.ImmutableSet;
import org.protege.owlapi.inference.cls.ChildClassExtractor;
import org.protege.owlapi.inference.cls.ParentClassExtractor;
import org.protege.owlapi.inference.orphan.TerminalElementFinder;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.search.EntitySearcher;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 17-Jan-2007<br><br>
 */
public class AssertedClassHierarchyProvider extends AbstractOWLObjectHierarchyProvider<OWLClass> {

    private final OWLOntologyManager owlOntologyManager;

    private final ReadLock ontologySetReadLock;

    private final WriteLock ontologySetWriteLock;

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
    private final Collection<OWLOntology> ontologies = new ArrayList<>();

    private volatile OWLClass root;

    private final ParentClassExtractor parentClassExtractor;

    private final ChildClassExtractor childClassExtractor;

    private final OWLOntologyChangeListener listener;

    private final TerminalElementFinder<OWLClass> rootFinder;

    private final Set<OWLClass> nodesToUpdate = new HashSet<>();

    public AssertedClassHierarchyProvider(@Nonnull OWLOntologyManager owlOntologyManager) {
        super(owlOntologyManager);
        this.owlOntologyManager = checkNotNull(owlOntologyManager);
        ReentrantReadWriteLock locks = new ReentrantReadWriteLock();
        ontologySetReadLock = locks.readLock();
        ontologySetWriteLock = locks.writeLock();
        rootFinder = new TerminalElementFinder<>(cls -> {
            Collection<OWLClass> parents = getParents(cls);
            parents.remove(root);
            return parents;
        });
        parentClassExtractor = new ParentClassExtractor();
        childClassExtractor = new ChildClassExtractor();
        listener = this::handleChanges;
        getManager().addOntologyChangeListener(listener);
    }

    /**
     * Sets the ontologies that this hierarchy provider should use
     * in order to determine the hierarchy.
     */
    public void setOntologies(Set<OWLOntology> ontologies) {
        ontologySetWriteLock.lock();
        try {
            /*
    		 * It is not safe to set the collection of ontologies to a HashSet or TreeSet.  
    		 * When an ontology changes name it gets a new Hash Code and it is sorted 
    		 * differently, so these Collections do not work.
    		 */
            this.ontologies.clear();
            this.ontologies.addAll(ontologies);
//            ImmutableSet<OWLObjectProperty> props = ImmutableSet.copyOf(ontologies.stream().flatMap(o -> o.getObjectPropertiesInSignature().stream()).collect(toSet()));
//            childClassExtractor.setRelationshipProperties(props);
            nodesToUpdate.clear();
            if (root == null) {
                root = owlOntologyManager.getOWLDataFactory().getOWLThing();
            }
            rebuildImplicitRoots();
            fireHierarchyChanged();
        } finally {
            ontologySetWriteLock.unlock();
        }
    }

    private void rebuildImplicitRoots() {
        ontologySetReadLock.lock();
        try {
            rootFinder.clear();
            for (OWLOntology ont : ontologies) {
                Set<OWLClass> ref = ont.getClassesInSignature();
                rootFinder.appendTerminalElements(ref);
            }
            rootFinder.finish();
        } finally {
            ontologySetReadLock.unlock();
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
        Set<OWLClass> oldTerminalElements = new HashSet<>(rootFinder.getTerminalElements());
        Set<OWLClass> changedClasses = new HashSet<>();
        changedClasses.add(root);
        List<OWLAxiomChange> filteredChanges = filterIrrelevantChanges(changes);
        updateImplicitRoots(filteredChanges);
        for (OWLOntologyChange change : filteredChanges) {
            changedClasses.addAll(
                    change.getSignature().stream()
                    .filter(entity -> entity instanceof OWLClass)
                    .filter(entity -> !entity.equals(root))
                    .map(entity -> (OWLClass) entity)
                    .collect(Collectors.toList()));
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
        List<OWLAxiomChange> filteredChanges = new ArrayList<>();
        for (OWLOntologyChange change : changes) {
            // only listen for changes on the appropriate ontologies
            if (ontologies.contains(change.getOntology())) {
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
        for (OWLClass node : nodesToUpdate) {
            fireNodeChanged(node);
        }
        nodesToUpdate.clear();
    }


    private void updateImplicitRoots(List<OWLAxiomChange> changes) {
        Set<OWLClass> possibleTerminalElements = new HashSet<>();
        Set<OWLClass> notInOntologies = new HashSet<>();

        for (OWLOntologyChange change : changes) {
            // only listen for changes on the appropriate ontologies
            if (ontologies.contains(change.getOntology())) {
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

    protected Collection<OWLClass> getUnfilteredChildren(OWLClass object) {
        ontologySetReadLock.lock();
        try {
            if (object.equals(root)) {
                Set<OWLClass> result = new HashSet<>();
                result.addAll(rootFinder.getTerminalElements());
                result.addAll(extractChildren(object));
                result.remove(object);
                return result;
            }
            else {
                return extractChildren(object);
            }
        } finally {
            ontologySetReadLock.unlock();
        }
    }

    private Collection<OWLClass> extractChildren(OWLClass parent) {
        childClassExtractor.setCurrentParentClass(parent);
        for (OWLOntology ont : ontologies) {
            for (OWLAxiom ax : ont.getReferencingAxioms(parent)) {
                if (ax.isLogicalAxiom()) {
                    ax.accept(childClassExtractor);
                }
            }
        }
        // Expand results with synonyms that are given via EquivalentClasses(A B)
        Collection<OWLClass> result = childClassExtractor.getResult();
        Set<OWLClass> synonyms = new HashSet<>();
        for(OWLOntology ont : ontologies) {
            for(OWLClass cls : result) {
                ont.getEquivalentClassesAxioms(cls)
                        .stream()
                        .map(ax -> ax.getClassExpressions())
                        .filter(classExpressions -> classExpressions.stream().noneMatch(OWLClassExpression::isAnonymous))
                        .flatMap(classExpressions -> classExpressions.stream())
                        .map(ce -> ce.asOWLClass())
                        .filter(ce -> !ce.equals(cls))
                        .forEach(synonyms::add);
            }
        }
        result.addAll(synonyms);
        return result;
    }

    public boolean containsReference(OWLClass object) {
        ontologySetReadLock.lock();
        try {
            for (OWLOntology ont : ontologies) {
                if (ont.containsClassInSignature(object.getIRI())) {
                    return true;
                }
            }
            return false;
        } finally {
            ontologySetReadLock.unlock();
        }
    }


    public Set<OWLClass> getParents(OWLClass object) {
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
                return result;
            }
            // Not a root, so must have another parent
            parentClassExtractor.reset();
            parentClassExtractor.setCurrentClass(object);
            for (OWLOntology ont : ontologies) {
                for (OWLAxiom ax : ont.getAxioms(object, Imports.EXCLUDED)) {
                    ax.accept(parentClassExtractor);
                }
            }
            result.addAll(parentClassExtractor.getResult());
            return result;
        } finally {
            ontologySetReadLock.unlock();
        }
    }


    public Set<OWLClass> getEquivalents(OWLClass object) {
        ontologySetReadLock.lock();
        try {
            Set<OWLClass> result = new HashSet<>();
            for (OWLOntology ont : ontologies) {
                for (OWLClassExpression equiv : EntitySearcher.getEquivalentClasses(object, ont)) {
                    if (!equiv.isAnonymous()) {
                        result.add((OWLClass) equiv);
                    }
                }
            }
            return result;
        } finally {
            ontologySetReadLock.unlock();
        }
    }

    public void setDisplayedRelationships(Set<OWLObjectProperty> properties) {
        this.childClassExtractor.setRelationshipProperties(ImmutableSet.copyOf(properties));
        fireHierarchyChanged();
    }

    @Override
    public Set<?> getDisplayedRelationships() {
        return childClassExtractor.getRelationships();
    }

    @Override
    public Optional<?> getRelationship(OWLClass parent, OWLClass child) {
        if(parent.isOWLThing()) {
            return Optional.empty();
        }
        if(childClassExtractor.getRelationships().isEmpty()) {
            return Optional.empty();
        }
        if (childClassExtractor.getCurrentParentClass().equals(Optional.of(parent))) {
            extractChildren(parent);
        }
        return childClassExtractor.getRelationship(child);
    }
}
