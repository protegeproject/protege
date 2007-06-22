package org.protege.editor.owl.ui.list;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLObject;

import javax.swing.*;
import java.awt.*;
import java.util.Set;
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
public class OWLObjectListPanel<E extends OWLObject> extends JPanel {

    private OWLObjectList list;


    public OWLObjectListPanel(Set<E> objects, OWLEditorKit owlEditorKit) {
        this(null, objects, owlEditorKit);
    }


    public OWLObjectListPanel(String message, Set<E> objects, OWLEditorKit owlEditorKit) {
        setLayout(new BorderLayout(7, 7));
        if (message != null) {
            add(new JLabel("<html><body>" + message + "</body></html>"), BorderLayout.NORTH);
        }
        list = new OWLObjectList(owlEditorKit);
        list.setListData(objects.toArray());
        add(ComponentFactory.createScrollPane(list));
    }


    public E getSelectedObject() {
        return (E) list.getSelectedValue();
    }


    public void setSelectedObject(E object) {
        list.setSelectedValue(object, true);
    }
}
