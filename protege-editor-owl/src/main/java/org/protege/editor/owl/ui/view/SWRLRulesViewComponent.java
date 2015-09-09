package org.protege.editor.owl.ui.view;

import org.protege.editor.owl.ui.frame.SWRLRulesFrame;
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
 * Date: 06-Jul-2007<br><br>
 */
public class SWRLRulesViewComponent extends AbstractActiveOntologyViewComponent {

    /**
     * 
     */
    private static final long serialVersionUID = -204496848858955147L;

    private OWLFrameList list;

    private SWRLRulesFrame frame;


    protected void initialiseOntologyView() throws Exception {
        frame = new SWRLRulesFrame(getOWLEditorKit());
        list = new OWLFrameList(getOWLEditorKit(), frame);
        setLayout(new BorderLayout());
        add(new JScrollPane(list));
        updateView(getOWLEditorKit().getModelManager().getActiveOntology());
    }


    protected void disposeOntologyView() {
        list.dispose();
    }


    protected void updateView(OWLOntology activeOntology) throws Exception {
        list.setRootObject(activeOntology);
    }
}
