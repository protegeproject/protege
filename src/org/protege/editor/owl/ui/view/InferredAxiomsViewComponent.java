package org.protege.editor.owl.ui.view;

import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.frame.InferredAxiomsFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.semanticweb.owlapi.model.OWLOntology;

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
 * Date: 14-Oct-2007<br><br>
 */
public class InferredAxiomsViewComponent extends AbstractActiveOntologyViewComponent {

    private InferredAxiomsFrame frame;

    private OWLFrameList<OWLOntology> frameList;

    private OWLModelManagerListener listener = new OWLModelManagerListener() {

        public void handleChange(OWLModelManagerChangeEvent event) {
            if(event.isType(EventType.ONTOLOGY_CLASSIFIED)) {
                if(isSynchronizing()) {
                    try {
                            updateView(getOWLModelManager().getActiveOntology());
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    };

    protected void initialiseOntologyView() throws Exception {
        setLayout(new BorderLayout());
        frame = new InferredAxiomsFrame(getOWLEditorKit());
        frameList = new OWLFrameList<OWLOntology>(getOWLEditorKit(), frame);
        frameList.setRootObject(getOWLModelManager().getActiveOntology());
        updateHeader();
        add(new JScrollPane(frameList));
        getOWLModelManager().addListener(listener);
    }


    protected void updateView(OWLOntology activeOntology) throws Exception {
        if (isSynchronizing()) {
            frameList.setRootObject(activeOntology);
            updateHeader();
        }
    }


    private void updateHeader() {
        getView().setHeaderText("Classified using " + getOWLModelManager().getOWLReasonerManager().getCurrentReasonerName());
    }


    protected void disposeOntologyView() {
        frameList.dispose();
        getOWLModelManager().removeListener(listener);
    }
}
