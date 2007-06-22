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
 * Date: 06-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class InferredOWLClassHierarchyProvider extends AbstractOWLObjectHierarchyProvider<OWLClass> {

    private static final Logger logger = Logger.getLogger(InferredOWLClassHierarchyProvider.class);

    private OWLModelManager owlModelManager;

    private OWLClass root;

    private OWLModelManagerListener owlModelManagerListener;


    public InferredOWLClassHierarchyProvider(OWLModelManager owlModelManager, OWLOntologyManager owlOntologyManager) {
        super(owlOntologyManager);
        this.owlModelManager = owlModelManager;
        root = owlModelManager.getOWLDataFactory().getOWLThing();
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
        return Collections.singleton(root);
    }


    protected OWLReasoner getReasoner() {
        return owlModelManager.getOWLReasonerManager().getCurrentReasoner();
    }


    public Set<OWLClass> getChildren(OWLClass object) {
        try {
            Set<OWLClass> subs = OWLReasonerAdapter.flattenSetOfSets(getReasoner().getSubClasses(object));
            // Add in owl:Nothing if there are inconsistent classes
            if (object.isOWLThing() && !owlModelManager.getReasoner().getInconsistentClasses().isEmpty()) {
                subs.add(owlModelManager.getOWLDataFactory().getOWLNothing());
            }
            else if (object.equals(owlModelManager.getOWLDataFactory().getOWLNothing())) {
                subs.addAll(getReasoner().getInconsistentClasses());
                subs.remove(owlModelManager.getOWLDataFactory().getOWLNothing());
            }
            else {
                // Class which is not Thing or Nothing
                subs.remove(owlModelManager.getOWLDataFactory().getOWLNothing());
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
