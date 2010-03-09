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
        return getOWLModelManager().getRendering(o1).compareTo(getOWLModelManager().getRendering(o2));
    }
}