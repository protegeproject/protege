package org.protege.editor.owl.ui.view;

/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */
import java.awt.BorderLayout;

import javax.swing.JScrollPane;

import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.protege.editor.owl.ui.frame.OWLEntityFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.semanticweb.owlapi.model.OWLEntity;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 11-Jul-2007<br><br>
 */
public class OWLEntityViewComponent extends AbstractOWLViewComponent {


    private OWLFrameList<OWLEntity> list;

    private OWLSelectionModelListener listener = () -> updateFrame();


    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout());
        list = new OWLFrameList<>(getOWLEditorKit(), new OWLEntityFrame(getOWLEditorKit()));
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
