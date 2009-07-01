package org.protege.editor.owl.ui.view;

import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.protege.editor.owl.ui.frame.OWLEntityFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.swing.*;
import java.awt.*;
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
 * Date: 11-Jul-2007<br><br>
 */
public class OWLEntityViewComponent extends AbstractOWLViewComponent {

    private OWLFrameList<OWLEntity> list;

    private OWLSelectionModelListener listener = new OWLSelectionModelListener() {

        public void selectionChanged() throws Exception {
            updateFrame();
        }
    };


    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout());
        list = new OWLFrameList<OWLEntity>(getOWLEditorKit(), new OWLEntityFrame(getOWLEditorKit()));
        updateFrame();
        getOWLWorkspace().getOWLSelectionModel().addListener(listener);
        add(new JScrollPane(list));
    }


    private void updateFrame() {
        OWLEntity selEntity = getOWLWorkspace().getOWLSelectionModel().getSelectedEntity();
        list.setRootObject(selEntity);
    }


    protected void disposeOWLView() {
        getOWLWorkspace().getOWLSelectionModel().removeListener(listener);
    }
}
