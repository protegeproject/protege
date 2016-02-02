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
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 28-Oct-2007<br><br>
 */
public class SelectedObjectViewComponent extends AbstractOWLViewComponent {


    private OWLEntityFrame entityFrame;

    private OWLFrameList frameList;


    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout());
        entityFrame = new OWLEntityFrame(getOWLEditorKit());
        frameList = new OWLFrameList(getOWLEditorKit(), entityFrame);
        add(new JScrollPane(frameList));
        getOWLWorkspace().getOWLSelectionModel().addListener(() -> {
            frameList.setRootObject(getOWLWorkspace().getOWLSelectionModel().getSelectedObject());
        });
        frameList.setRootObject(null);
        getOWLModelManager().addListener(event -> {
            if(event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED)) {
                frameList.setRootObject(getOWLModelManager().getActiveOntology());
            }
        });
    }


    protected void disposeOWLView() {
    }
}
