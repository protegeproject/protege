package org.protege.editor.owl.ui.list;

import java.awt.Dimension;
import java.util.Set;
import java.util.TreeSet;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLEntity;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 19-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLEntityListPanel<E extends OWLEntity> extends OWLObjectListPanel<E> {

    public OWLEntityListPanel(Set<E> objects, OWLEditorKit owlEditorKit) {
        super(objects, owlEditorKit);
    }


    public OWLEntityListPanel(String message, Set<E> objects, OWLEditorKit owlEditorKit) {
        super(message, getOrderedSet(owlEditorKit.getModelManager(), objects), owlEditorKit);
    }


    private static <E extends OWLEntity> Set<E> getOrderedSet(OWLModelManager owlModelManager, Set<E> objects) {
        TreeSet<E> ts = new TreeSet<>(owlModelManager.getOWLObjectComparator());
        ts.addAll(objects);
        return ts;
    }


    public Dimension getPreferredSize() {
        return new Dimension(300, 500);
    }
}
