package org.protege.editor.owl.model;

import org.semanticweb.owl.model.OWLOntology;

import java.util.*;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 20, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OntologyVisibilityManagerImpl implements OntologyVisibilityManager {

    private Set<OWLOntology> visibleOntologies;

    private List<OntologyVisibilityManagerListener> listeners;


    public OntologyVisibilityManagerImpl() {
        visibleOntologies = new HashSet<OWLOntology>();
        listeners = new ArrayList<OntologyVisibilityManagerListener>();
    }


    public Set<OWLOntology> getVisibleOntologies() {
        return Collections.unmodifiableSet(visibleOntologies);
    }


    public boolean isVisible(OWLOntology ontology) {
        return visibleOntologies.contains(ontology);
    }


    public void setVisible(Set<OWLOntology> ontologies) {
        if (visibleOntologies.equals(ontologies) == false) {
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
        List<OntologyVisibilityManagerListener> listenersCopy = new ArrayList<OntologyVisibilityManagerListener>(
                listeners);
        for (OntologyVisibilityManagerListener listener : listenersCopy) {
            listener.ontologyVisibilityChanged(this);
        }
    }
}
