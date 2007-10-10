package org.protege.editor.owl.model.hierarchy;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.model.OWLOntologyChangeListener;
import org.semanticweb.owl.model.OWLOntologyManager;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 01-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class AbstractOWLOntologyObjectHierarchyProvider<N extends OWLObject> extends AbstractOWLObjectHierarchyProvider<N> implements OWLOntologyObjectHierarchyProvider<N> {

    public static final Logger logger = Logger.getLogger(AbstractOWLOntologyObjectHierarchyProvider.class);

    private Set<OWLOntology> ontologies;

    private OWLOntologyChangeListener listener;


    protected AbstractOWLOntologyObjectHierarchyProvider(OWLOntologyManager manager) {
        super(manager);
        ontologies = new HashSet<OWLOntology>();
        listener = new OWLOntologyChangeListener() {
            /**
             * Called when some changes have been applied to various ontologies.  These
             * may be an axiom added or an axiom removed changes.
             * @param changes A list of changes that have occurred.  Each change may be examined
             *                to determine which ontology it was applied to.
             */
            public void ontologiesChanged(List<? extends OWLOntologyChange> changes) {
                handleOntologyChanges(changes);
            }
        };
        attachListeners();
    }


    public Set<OWLOntology> getOntologies() {
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
