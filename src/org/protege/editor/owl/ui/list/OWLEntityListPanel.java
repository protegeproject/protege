package org.protege.editor.owl.ui.list;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.OWLEntityComparator;
import org.semanticweb.owl.model.OWLEntity;

import java.awt.*;
import java.util.Set;
import java.util.TreeSet;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 19-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLEntityListPanel<E extends OWLEntity> extends OWLObjectListPanel<E> {

    public OWLEntityListPanel(Set<E> objects, OWLEditorKit owlEditorKit) {
        super(objects, owlEditorKit);
    }


    public OWLEntityListPanel(String message, Set<E> objects, OWLEditorKit owlEditorKit) {
        super(message, getOrderedSet(owlEditorKit.getOWLModelManager(), objects), owlEditorKit);
    }


    private static <E extends OWLEntity> Set<E> getOrderedSet(OWLModelManager owlModelManager, Set<E> objects) {
        TreeSet ts = new TreeSet(new OWLEntityComparator<E>(owlModelManager));
        ts.addAll(objects);
        return ts;
    }


    public Dimension getPreferredSize() {
        return new Dimension(300, 500);
    }
}
