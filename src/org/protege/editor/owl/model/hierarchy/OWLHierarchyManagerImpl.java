package org.protege.editor.owl.model.hierarchy;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.hierarchy.cls.InferredOWLClassHierarchyProvider;
import org.protege.editor.owl.model.hierarchy.property.InferredObjectPropertyHierarchyProvider;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
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

    private OWLObjectHierarchyProvider<OWLObjectProperty> assertedObjectPropertyHierarchyProvider;

    private OWLObjectHierarchyProvider<OWLObjectProperty> inferredObjectPropertyHierarchyProvider;

    private OWLObjectHierarchyProvider<OWLDataProperty> assertedDataPropertyHierarchyProvider;

    private OWLAnnotationPropertyHierarchyProvider assertedAnnotationPropertyHierarchyProvider;

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
            assertedClassHierarchyProvider = new AssertedClassHierarchyProvider(mngr.getOWLOntologyManager());
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
        if (assertedObjectPropertyHierarchyProvider == null) {
            assertedObjectPropertyHierarchyProvider = new OWLObjectPropertyHierarchyProvider(mngr.getOWLOntologyManager());
            assertedObjectPropertyHierarchyProvider.setOntologies(mngr.getActiveOntologies());
        }
        return assertedObjectPropertyHierarchyProvider;
    }


    public OWLObjectHierarchyProvider<OWLDataProperty> getOWLDataPropertyHierarchyProvider() {
        if (assertedDataPropertyHierarchyProvider == null) {
            assertedDataPropertyHierarchyProvider = new OWLDataPropertyHierarchyProvider(mngr.getOWLOntologyManager());
            assertedDataPropertyHierarchyProvider.setOntologies(mngr.getActiveOntologies());
        }
        return assertedDataPropertyHierarchyProvider;
    }


    public OWLAnnotationPropertyHierarchyProvider getOWLAnnotationPropertyHierarchyProvider() {
        if (assertedAnnotationPropertyHierarchyProvider == null){
            assertedAnnotationPropertyHierarchyProvider = new OWLAnnotationPropertyHierarchyProvider(mngr.getOWLOntologyManager());
            assertedAnnotationPropertyHierarchyProvider.setOntologies(mngr.getOntologies());
        }
        return assertedAnnotationPropertyHierarchyProvider;
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
        if (assertedObjectPropertyHierarchyProvider != null) {
            assertedObjectPropertyHierarchyProvider.dispose();
        }
        if (inferredObjectPropertyHierarchyProvider != null) {
            inferredObjectPropertyHierarchyProvider.dispose();
        }
        if (assertedDataPropertyHierarchyProvider != null) {
            assertedDataPropertyHierarchyProvider.dispose();
        }
    }


    private void rebuildAsNecessary() {
        // Rebuild the various hierarchies
        if (assertedClassHierarchyProvider != null) {
            getOWLClassHierarchyProvider().setOntologies(mngr.getActiveOntologies());
        }
        if (assertedObjectPropertyHierarchyProvider != null) {
            getOWLObjectPropertyHierarchyProvider().setOntologies(mngr.getActiveOntologies());
        }
        if (assertedDataPropertyHierarchyProvider != null) {
            getOWLDataPropertyHierarchyProvider().setOntologies(mngr.getActiveOntologies());
        }
    }
}
