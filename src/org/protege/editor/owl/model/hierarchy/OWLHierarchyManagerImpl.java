package org.protege.editor.owl.model.hierarchy;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.hierarchy.cls.InferredOWLClassHierarchyProvider;
import org.protege.editor.owl.model.hierarchy.property.InferredObjectPropertyHierarchyProvider;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLObjectProperty;
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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Nov 27, 2008<br><br>
 */
public class OWLHierarchyManagerImpl implements OWLHierarchyManager {


    private OWLObjectHierarchyProvider<OWLClass> assertedClassHierarchyProvider;

    private InferredOWLClassHierarchyProvider inferredClassHierarchyProvider;

    private OWLObjectHierarchyProvider<OWLObjectProperty> toldObjectPropertyHierarchyProvider;

    private OWLObjectHierarchyProvider<OWLObjectProperty> inferredObjectPropertyHierarchyProvider;

    private OWLObjectHierarchyProvider<OWLDataProperty> toldDataPropertyHierarchyProvider;

    private OWLModelManager mngr;

    private OWLModelManagerListener listener = new OWLModelManagerListener(){

        public void handleChange(OWLModelManagerChangeEvent event) {
            if (event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED)){
                rebuildAsNecessary();
            }
        }
    };


    public OWLHierarchyManagerImpl(OWLModelManager mngr) {
        this.mngr = mngr;
        mngr.addListener(listener);
    }


    public OWLObjectHierarchyProvider<OWLClass> getOWLClassHierarchyProvider() {
        if (assertedClassHierarchyProvider == null) {
            assertedClassHierarchyProvider = new AssertedClassHierarchyProvider2(mngr.getOWLOntologyManager());
            assertedClassHierarchyProvider.setOntologies(mngr.getActiveOntologies());
        }
        return assertedClassHierarchyProvider;
    }


    public OWLObjectHierarchyProvider<OWLClass> getInferredOWLClassHierarchyProvider() {
        if (inferredClassHierarchyProvider == null) {
            inferredClassHierarchyProvider = new InferredOWLClassHierarchyProvider(mngr, mngr.getOWLOntologyManager());
        }
        return inferredClassHierarchyProvider;
    }


    public OWLObjectHierarchyProvider<OWLObjectProperty> getOWLObjectPropertyHierarchyProvider() {
        if (toldObjectPropertyHierarchyProvider == null) {
            toldObjectPropertyHierarchyProvider = new OWLObjectPropertyHierarchyProvider(mngr.getOWLOntologyManager());
            toldObjectPropertyHierarchyProvider.setOntologies(mngr.getActiveOntologies());
        }
        return toldObjectPropertyHierarchyProvider;
    }


    public OWLObjectHierarchyProvider<OWLDataProperty> getOWLDataPropertyHierarchyProvider() {
        if (toldDataPropertyHierarchyProvider == null) {
            toldDataPropertyHierarchyProvider = new OWLDataPropertyHierarchyProvider(mngr.getOWLOntologyManager());
            toldDataPropertyHierarchyProvider.setOntologies(mngr.getActiveOntologies());
        }
        return toldDataPropertyHierarchyProvider;
    }


    public OWLObjectHierarchyProvider<OWLObjectProperty> getInferredOWLObjectPropertyHierarchyProvider() {
        if (inferredObjectPropertyHierarchyProvider == null){
            inferredObjectPropertyHierarchyProvider = new InferredObjectPropertyHierarchyProvider(mngr);
            inferredObjectPropertyHierarchyProvider.setOntologies(mngr.getActiveOntologies());
        }
        return inferredObjectPropertyHierarchyProvider;

    }


    public void dispose() throws Exception {
        mngr.removeListener(listener);

        if (assertedClassHierarchyProvider != null) {
            assertedClassHierarchyProvider.dispose();
        }
        if (inferredClassHierarchyProvider != null) {
            inferredClassHierarchyProvider.dispose();
        }
        if (toldObjectPropertyHierarchyProvider != null) {
            toldObjectPropertyHierarchyProvider.dispose();
        }
        if (inferredObjectPropertyHierarchyProvider != null) {
            inferredObjectPropertyHierarchyProvider.dispose();
        }
        if (toldDataPropertyHierarchyProvider != null) {
            toldDataPropertyHierarchyProvider.dispose();
        }
    }


    private void rebuildAsNecessary() {
        // Rebuild the various hierarchies
        if (assertedClassHierarchyProvider != null) {
            getOWLClassHierarchyProvider().setOntologies(mngr.getActiveOntologies());
        }
        if (toldObjectPropertyHierarchyProvider != null) {
            getOWLObjectPropertyHierarchyProvider().setOntologies(mngr.getActiveOntologies());
        }
        if (toldDataPropertyHierarchyProvider != null) {
            getOWLDataPropertyHierarchyProvider().setOntologies(mngr.getActiveOntologies());
        }
    }
}
