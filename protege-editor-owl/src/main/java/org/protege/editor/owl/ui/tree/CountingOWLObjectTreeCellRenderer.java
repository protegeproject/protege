package org.protege.editor.owl.ui.tree;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLObject;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jul 27, 2009<br><br>
 */
public class CountingOWLObjectTreeCellRenderer<N extends OWLObject> extends OWLObjectTreeCellRenderer {

    private OWLObjectTree<N> tree;


    public CountingOWLObjectTreeCellRenderer(OWLEditorKit owlEditorKit, OWLObjectTree<N> tree) {
        super(owlEditorKit);
        this.tree = tree;
    }

    protected String getRendering(Object object) {
        StringBuilder label = new StringBuilder(super.getRendering(object));
        int size = tree.getProvider().getChildren((N)object).size();
        if (size > 0){
            label.append(" (");
            label.append(size);
            label.append(")");
        }
        return label.toString();
    }
}
