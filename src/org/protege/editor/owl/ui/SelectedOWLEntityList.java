package org.protege.editor.owl.ui;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.semanticweb.owl.model.OWLEntity;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
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
 * Bio-Health Informatics Group<br>
 * Date: 08-Feb-2007<br><br>
 */
public class SelectedOWLEntityList extends JList implements OWLSelectionModelListener {

    private OWLEditorKit owlEditorKit;

    private List<OWLEntity> selectionList;


    /**
     * Constructs a <code>JList</code> with an empty model.
     */
    public SelectedOWLEntityList(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
        selectionList = new ArrayList<OWLEntity>();
        owlEditorKit.getOWLWorkspace().getOWLSelectionModel().addListener(this);
    }


    public void dispose() {
        owlEditorKit.getOWLWorkspace().getOWLSelectionModel().removeListener(this);
    }


    public void selectionChanged() {
        selectionList.add(0, owlEditorKit.getOWLWorkspace().getOWLSelectionModel().getSelectedEntity());
        if (selectionList.size() > 10) {
            selectionList.remove(selectionList.size() - 1);
        }
        setListData(selectionList.toArray());
    }
}
