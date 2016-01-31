package org.protege.editor.owl.ui.clsdescriptioneditor;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLObject;

import java.util.Set;
import java.util.TreeSet;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: May 4, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class AutoCompleterMatcherImpl implements AutoCompleterMatcher {

    private OWLModelManager owlModelManager;


    public AutoCompleterMatcherImpl(OWLModelManager owlModelManager) {
        this.owlModelManager = owlModelManager;
    }


    public Set<OWLObject> getMatches(String fragment, boolean classes, boolean objectProperties, boolean dataProperties,
    		                         boolean individuals, boolean datatypes, boolean annotationProperties) {
        TreeSet<OWLObject> set = new TreeSet<OWLObject>(owlModelManager.getOWLObjectComparator());

        fragment = fragment + "*"; // look for strings that start with the given fragment

        if (classes) {
            set.addAll(owlModelManager.getOWLEntityFinder().getMatchingOWLClasses(fragment, false));
        }
        if (objectProperties) {
            set.addAll(owlModelManager.getOWLEntityFinder().getMatchingOWLObjectProperties(fragment, false));
        }
        if (dataProperties) {
            set.addAll(owlModelManager.getOWLEntityFinder().getMatchingOWLDataProperties(fragment, false));
        }
        if (individuals) {
            set.addAll(owlModelManager.getOWLEntityFinder().getMatchingOWLIndividuals(fragment, false));
        }
        if (datatypes) {
            set.addAll(owlModelManager.getOWLEntityFinder().getMatchingOWLDatatypes(fragment, false));
        }
        if (annotationProperties) {
            set.addAll(owlModelManager.getOWLEntityFinder().getMatchingOWLAnnotationProperties(fragment, false));
        }
        return set;
    }
}
