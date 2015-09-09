package org.protege.editor.owl.ui;

import org.protege.editor.owl.OWLEditorKit;

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
public class ObjectSelectorPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -7757831439570646936L;


    private OWLEditorKit owlEditorKit;


    private OntologyImportsAndNavigationPanel ontologiesPanel;

    private OWLEntitySelectorPanel entitiesPanel;


    public ObjectSelectorPanel(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
        ontologiesPanel = new OntologyImportsAndNavigationPanel(owlEditorKit);
        setLayout(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        add(splitPane);
        splitPane.setTopComponent(ontologiesPanel);
        entitiesPanel = new OWLEntitySelectorPanel(owlEditorKit);
        splitPane.setBottomComponent(entitiesPanel);
        splitPane.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        splitPane.setResizeWeight(0.18);

    }
}
