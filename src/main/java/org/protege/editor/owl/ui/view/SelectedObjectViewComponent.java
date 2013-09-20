package org.protege.editor.owl.ui.view;

import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.protege.editor.owl.ui.frame.OWLEntityFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;

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
 * Date: 28-Oct-2007<br><br>
 */
public class SelectedObjectViewComponent extends AbstractOWLViewComponent {

    /**
     * 
     */
    private static final long serialVersionUID = 3043074017840450790L;

    private OWLEntityFrame entityFrame;

    private OWLFrameList frameList;


    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout());
        entityFrame = new OWLEntityFrame(getOWLEditorKit());
        frameList = new OWLFrameList(getOWLEditorKit(), entityFrame);
        add(new JScrollPane(frameList));
        getOWLWorkspace().getOWLSelectionModel().addListener(new OWLSelectionModelListener() {

            public void selectionChanged() throws Exception {
                frameList.setRootObject(getOWLWorkspace().getOWLSelectionModel().getSelectedObject());
            }
        });
        frameList.setRootObject(null);
        getOWLModelManager().addListener(new OWLModelManagerListener() {

            public void handleChange(OWLModelManagerChangeEvent event) {
                if(event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED)) {
                    frameList.setRootObject(getOWLModelManager().getActiveOntology());
                }
            }
        });
    }


    protected void disposeOWLView() {
    }
}
