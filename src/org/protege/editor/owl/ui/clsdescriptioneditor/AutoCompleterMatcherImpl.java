package org.protege.editor.owl.ui.clsdescriptioneditor;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLObject;


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
    		                         boolean individuals, boolean datatypes) {
        TreeSet<OWLObject> set = new TreeSet<OWLObject>(new Comparator<OWLObject>() {
            public int compare(OWLObject o1, OWLObject o2) {
                String ren1 = owlModelManager.getRendering(o1);
                String ren2 = owlModelManager.getRendering(o2);
                return ren1.compareTo(ren2);
            }
        });
        if (classes) {
            set.addAll(owlModelManager.getMatchingOWLClasses(fragment));
        }
        if (objectProperties) {
            set.addAll(owlModelManager.getMatchingOWLObjectProperties(fragment));
        }
        if (dataProperties) {
            set.addAll(owlModelManager.getMatchingOWLDataProperties(fragment));
        }
        if (individuals) {
            set.addAll(owlModelManager.getMatchingOWLIndividuals(fragment));
        }

        if (datatypes) {
            set.addAll(owlModelManager.getMatchingOWLDataTypes(fragment));
        }
        return set;
    }
}
