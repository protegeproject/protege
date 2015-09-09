package org.protege.editor.owl.ui.view;

import org.protege.editor.owl.ui.ObjectSelectorPanel;

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
public class ObjectSelectorViewComponent extends AbstractOWLViewComponent {


    /**
     * 
     */
    private static final long serialVersionUID = 3305793446146726647L;


    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout());
        add(new ObjectSelectorPanel(getOWLEditorKit()));
    }


    protected void disposeOWLView() {
    }
}
