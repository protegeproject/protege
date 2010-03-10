/*
 * Copyright (c) 2010 The University of Manchester.
 */

package org.protege.editor.owl.ui;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLObject;


public class OWLObjectRenderingComparator<E extends OWLObject> extends OWLObjectComparator<E> {

    public OWLObjectRenderingComparator(OWLModelManager owlModelManager) {
        super(owlModelManager);
    }

    public int compare(E o1, E o2) {
        String r1 = getOWLModelManager().getRendering(o1);
        if (r1.startsWith("'")){
            r1 = r1.substring(1, r1.length()-2);
        }
        String r2 = getOWLModelManager().getRendering(o2);
        if (r2.startsWith("'")){
            r2 = r2.substring(1, r2.length()-2);
        }
        return r1.compareTo(r2);
    }
}