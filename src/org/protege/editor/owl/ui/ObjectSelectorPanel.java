package org.protege.editor.owl.ui;

import org.protege.editor.owl.OWLEditorKit;

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
