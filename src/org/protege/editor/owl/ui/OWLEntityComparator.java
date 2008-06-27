package org.protege.editor.owl.ui;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLEntity;

import java.util.Comparator;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 27, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLEntityComparator<E extends OWLEntity> implements Comparator<E> {


    private OWLModelManager owlModelManager;


    public OWLEntityComparator(OWLModelManager owlModelManager) {
        this.owlModelManager = owlModelManager;
    }


    public int compare(E o1, E o2) {
        return owlModelManager.getRendering(o1).compareToIgnoreCase(owlModelManager.getRendering(o2));
    }
}
