package org.protege.editor.owl.ui;

import java.util.Comparator;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLObject;


/**
 * Author: Nick Drummond<br>
 * nick.drummond@cs.manchester.ac.uk<br>
 * http://www.cs.man.ac.uk/~drummond<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Dec 8, 2006<br><br>
 * <p/>
 * code made available under Mozilla Public License (http://www.mozilla.org/MPL/MPL-1.1.html)<br>
 * copyright 2006, The University of Manchester<br>
 */
public class OWLObjectComparator<E extends OWLObject> implements Comparator<E> {

    private OWLModelManager owlModelManager;


    public OWLObjectComparator(OWLModelManager owlModelManager) {
        this.owlModelManager = owlModelManager;
    }


    public int compare(OWLObject o1, OWLObject o2) {
        String ren1 = owlModelManager.getRendering(o1);
        String ren2 = owlModelManager.getRendering(o2);
        return ren1.compareToIgnoreCase(ren2);
    }
}
