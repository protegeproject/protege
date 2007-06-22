package org.protege.editor.owl.model.hierarchy;

import org.apache.log4j.Logger;
import org.semanticweb.owl.model.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
