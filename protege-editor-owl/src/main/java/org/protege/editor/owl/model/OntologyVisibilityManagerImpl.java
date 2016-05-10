package org.protege.editor.owl.model;

import org.semanticweb.owlapi.model.OWLOntology;

import java.util.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 20, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OntologyVisibilityManagerImpl implements OntologyVisibilityManager {

    private Set<OWLOntology> visibleOntologies;

    private List<OntologyVisibilityManagerListener> listeners;


    public OntologyVisibilityManagerImpl() {
        visibleOntologies = new HashSet<>();
        listeners = new ArrayList<>();
    }


    public Set<OWLOntology> getVisibleOntologies() {
        return Collections.unmodifiableSet(visibleOntologies);
    }


    public boolean isVisible(OWLOntology ontology) {
        return visibleOntologies.contains(ontology);
    }


    public void setVisible(Set<OWLOntology> ontologies) {
        if (!visibleOntologies.equals(ontologies)) {
            visibleOntologies.clear();
            visibleOntologies.addAll(ontologies);
            fireVisibilityChanged();
        }
    }


    public void setVisible(OWLOntology ontology, boolean b) {
        if (b) {
            if (visibleOntologies.add(ontology)) {
                fireVisibilityChanged();
            }
        }
        else {
            if (visibleOntologies.remove(ontology)) {
                fireVisibilityChanged();
            }
        }
    }


    public void addListener(OntologyVisibilityManagerListener listener) {
        listeners.add(listener);
    }


    public void removeListener(OntologyVisibilityManagerListener listener) {
        listeners.remove(listener);
    }


    protected void fireVisibilityChanged() {
        List<OntologyVisibilityManagerListener> listenersCopy = new ArrayList<>(
                listeners);
        for (OntologyVisibilityManagerListener listener : listenersCopy) {
            listener.ontologyVisibilityChanged(this);
        }
    }
}
