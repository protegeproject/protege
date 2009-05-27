package org.protege.editor.owl.model.hierarchy.cls;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.hierarchy.AbstractOWLObjectHierarchyProvider;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerAdapter;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLRuntimeException;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 06-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class InferredOWLClassHierarchyProvider extends AbstractOWLObjectHierarchyProvider<OWLClass> {

    private static final Logger logger = Logger.getLogger(InferredOWLClassHierarchyProvider.class);

    private OWLModelManager owlModelManager;

    private OWLClass owlThing;
    private OWLClass owlNothing;

    private OWLModelManagerListener owlModelManagerListener;


    public InferredOWLClassHierarchyProvider(OWLModelManager owlModelManager, OWLOntologyManager owlOntologyManager) {
        super(owlOntologyManager);
        this.owlModelManager = owlModelManager;

        owlThing = owlModelManager.getOWLDataFactory().getOWLThing();
        owlNothing = owlModelManager.getOWLDataFactory().getOWLNothing();

        owlModelManagerListener = new OWLModelManagerListener() {
            public void handleChange(OWLModelManagerChangeEvent event) {
                if (event.isType(EventType.REASONER_CHANGED) || event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED) || event.isType(
                        EventType.ONTOLOGY_CLASSIFIED)) {
                    fireHierarchyChanged();
                }
            }
        };
        owlModelManager.addListener(owlModelManagerListener);
    }


    public void rebuild() {
    }


    public void dispose() {
        super.dispose();
        owlModelManager.removeListener(owlModelManagerListener);
    }


    public Set<OWLClass> getSubPropertiesOfRoot() {
        return Collections.singleton(owlThing);
    }


    protected OWLReasoner getReasoner() {
        return owlModelManager.getOWLReasonerManager().getCurrentReasoner();
    }


    public Set<OWLClass> getChildren(OWLClass object) {
        try {
            Set<OWLClass> subs = OWLReasonerAdapter.flattenSetOfSets(getReasoner().getSubClasses(object));
            // Add in owl:Nothing if there are inconsistent classes
            if (object.isOWLThing() && !owlModelManager.getReasoner().getInconsistentClasses().isEmpty()) {
                subs.add(owlNothing);
            }
            else if (object.isOWLNothing()) {
                subs.addAll(getReasoner().getInconsistentClasses());
                subs.remove(owlNothing);
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
        catch (OWLReasonerException e) {
            throw new OWLRuntimeException(e);
        }
    }


    public Set<OWLClass> getDescendants(OWLClass object) {
        try {
            return OWLReasonerAdapter.flattenSetOfSets(getReasoner().getDescendantClasses(object));
        }
        catch (OWLReasonerException e) {
            throw new OWLRuntimeException(e);
        }
    }


    public Set<OWLClass> getParents(OWLClass object) {
        try {
            if (object.isOWLNothing()){
                return Collections.singleton(owlThing);
            }
            else if (!getReasoner().isSatisfiable(object)){
                return Collections.singleton(owlNothing);
            }
            Set<OWLClass> parents = OWLReasonerAdapter.flattenSetOfSets(getReasoner().getSuperClasses(object));
            parents.remove(object);
            return parents;
        }
        catch (OWLReasonerException e) {
            throw new OWLRuntimeException(e);
        }
    }


    public Set<OWLClass> getAncestors(OWLClass object) {
        try {
            return OWLReasonerAdapter.flattenSetOfSets(getReasoner().getAncestorClasses(object));
        }
        catch (OWLReasonerException e) {
            throw new OWLRuntimeException(e);
        }
    }


    public Set<OWLClass> getEquivalents(OWLClass object) {
        try {
            if (!getReasoner().isSatisfiable(object)) {
                return Collections.emptySet();
            }
            Set<OWLClass> equivalents = getReasoner().getEquivalentClasses(object);
            equivalents.remove(object);
            return equivalents;
        }
        catch (OWLReasonerException e) {
            throw new OWLRuntimeException(e);
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
        return Collections.EMPTY_SET;
    }


    /**
     * Sets the ontologies that this hierarchy provider should use
     * in order to determine the hierarchy.
     */
    public void setOntologies(Set<OWLOntology> ontologies) {
    }
}
