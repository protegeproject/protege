package org.protege.editor.owl.ui.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.frame.InferredAxiomsFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.semanticweb.owlapi.model.OWLOntology;


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

    /**
     * 
     */
    private static final long serialVersionUID = 7129182885438253297L;

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
        add(getCenterPane(), BorderLayout.CENTER);
        // considered adding a refresh button here...
        updateHeader();
        getOWLModelManager().addListener(listener);
    }
    
    private JComponent getCenterPane() {
        setLayout(new BorderLayout());
        frame = new InferredAxiomsFrame(getOWLEditorKit());
        frameList = new OWLFrameList<OWLOntology>(getOWLEditorKit(), frame);
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
