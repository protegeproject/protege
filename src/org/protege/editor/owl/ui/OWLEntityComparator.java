package org.protege.editor.owl.ui;

import java.util.Comparator;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.renderer.OWLEntityRenderer;
import org.semanticweb.owl.model.OWLEntity;


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


    //public int compare(OWLEntity o1, OWLEntity o2) {
    public int compare(E o1, E o2) {
        OWLEntityRenderer renderer = owlModelManager.getOWLEntityRenderer();
        return renderer.render(o1).compareToIgnoreCase(renderer.render(o2));
    }
}
