package org.protege.editor.owl.model.hierarchy;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/*
 * This is abstract but has no subclasses.  Looks like dead code?  I am not modifying this for thread safety yet.
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 01-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class AbstractOWLOntologyObjectHierarchyProvider<N extends OWLObject> extends AbstractOWLObjectHierarchyProvider<N> implements OWLOntologyObjectHierarchyProvider<N> {

    private Set<OWLOntology> ontologies;

    private OWLOntologyChangeListener listener;


    protected AbstractOWLOntologyObjectHierarchyProvider(OWLOntologyManager manager) {
        super(manager);
        ontologies = new HashSet<>();
        listener = changes -> handleOntologyChanges(changes);
        attachListeners();
    }


    public Collection<OWLOntology> getOntologies() {
        return Collections.unmodifiableSet(ontologies);
    }


    private void attachListeners() {
        getManager().addOntologyChangeListener(listener);
    }


    private void detachListeners() {
        getManager().removeOntologyChangeListener(listener);
    }

//    public void setOntologies(Set<OWLOntology> onts) {
//        detachListeners();
//        ontologies.clear();
//        this.ontologies.addAll(onts);
//
//        attachListeners();
//        fireHierarchyChanged();
//    }


    public void dispose() {
        super.dispose();
        detachListeners();
    }
}
