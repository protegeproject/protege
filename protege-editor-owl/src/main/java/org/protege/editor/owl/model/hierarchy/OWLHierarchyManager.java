package org.protege.editor.owl.model.hierarchy;

import org.protege.editor.core.Disposable;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Nov 27, 2008<br><br>
 */
public interface OWLHierarchyManager extends Disposable {

    String ID = OWLHierarchyManager.class.toString();

    /**
     * This returns the class hierarchy provider whose hierarchy is
     * generated from told information about the active ontologies.
     */
    OWLObjectHierarchyProvider<OWLClass> getOWLClassHierarchyProvider();


    OWLObjectHierarchyProvider<OWLClass> getInferredOWLClassHierarchyProvider();


    OWLObjectHierarchyProvider<OWLObjectProperty> getOWLObjectPropertyHierarchyProvider();


    OWLObjectHierarchyProvider<OWLObjectProperty> getInferredOWLObjectPropertyHierarchyProvider();


    OWLObjectHierarchyProvider<OWLDataProperty> getOWLDataPropertyHierarchyProvider();


    OWLAnnotationPropertyHierarchyProvider getOWLAnnotationPropertyHierarchyProvider();


    IndividualsByTypeHierarchyProvider getOWLIndividualsByTypeHierarchyProvider();    
}
