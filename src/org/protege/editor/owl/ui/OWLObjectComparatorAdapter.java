package org.protege.editor.owl.ui;

import org.semanticweb.owl.model.OWLObject;

import java.util.Comparator;


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
public class OWLObjectComparatorAdapter<E extends OWLObject> implements Comparator<E> {

    private Comparator<E> comp;

    public OWLObjectComparatorAdapter(Comparator<E> comp) {
        this.comp = comp;
    }

    public int compare(E o1, E o2) {
        return comp.compare(o1, o2);
    }
}