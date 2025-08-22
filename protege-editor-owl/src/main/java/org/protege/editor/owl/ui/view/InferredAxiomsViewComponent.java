package org.protege.editor.owl.ui.view;

import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.frame.InferredAxiomsFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.semanticweb.owlapi.model.OWLOntology;

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
 * Date: 14-Oct-2007<br><br>
 */
public class InferredAxiomsViewComponent extends AbstractActiveOntologyViewComponent {


    private InferredAxiomsFrame frame;

    private OWLFrameList<OWLOntology> frameList;

    private OWLModelManagerListener listener = event -> {
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
    };

    protected void initialiseOntologyView() throws Exception {
        add(getCenterPane(), BorderLayout.CENTER);
        // considered adding a refresh button here...
        updateHeader();
        getOWLModelManager().addListener(listener);
    }
    
    private JComponent getCenterPane() {
        setLayout(new BorderLayout());
        frame = new InferredAxiomsFrame(getOWLEditorKit());
        frameList = new OWLFrameList<>(getOWLEditorKit(), frame);
        frameList.setRootObject(getOWLModelManager().getActiveOntology());
        return new JScrollPane(frameList);
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
