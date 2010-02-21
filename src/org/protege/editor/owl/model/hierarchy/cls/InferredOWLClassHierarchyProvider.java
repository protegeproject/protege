package org.protege.editor.owl.model.hierarchy.cls;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
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


    public Set<OWLClass> getRoots() {
        return Collections.singleton(owlThing);
    }


    protected OWLReasoner getReasoner() {
        return owlModelManager.getOWLReasonerManager().getCurrentReasoner();
    }


    public Set<OWLClass> getChildren(OWLClass object) {
        Set<OWLClass> subs = getReasoner().getSubClasses(object, true).getFlattened();
        // Add in owl:Nothing if there are inconsistent classes
        if (object.isOWLThing() && !owlModelManager.getReasoner().getUnsatisfiableClasses().isSingleton()) {
            subs.add(owlNothing);
        }
        else if (object.isOWLNothing()) {
            subs.addAll(getReasoner().getUnsatisfiableClasses().getEntities());
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


    public Set<OWLClass> getDescendants(OWLClass object) {
        return getReasoner().getSubClasses(object, false).getFlattened();
    }


    public Set<OWLClass> getParents(OWLClass object) {
        if (object.isOWLNothing()){
            return Collections.singleton(owlThing);
        }
        else if (!getReasoner().isSatisfiable(object)){
            return Collections.singleton(owlNothing);
        }
        Set<OWLClass> parents = getReasoner().getSuperClasses(object, true).getFlattened();
        parents.remove(object);
        return parents;
    }


    public Set<OWLClass> getAncestors(OWLClass object) {
        return getReasoner().getSuperClasses(object, false).getFlattened();
    }


    public Set<OWLClass> getEquivalents(OWLClass object) {
        if (!getReasoner().isSatisfiable(object)) {
            return Collections.emptySet();
        }
        Set<OWLClass> equivalents = getReasoner().getEquivalentClasses(object).getEntities();
        equivalents.remove(object);
        return equivalents;
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
